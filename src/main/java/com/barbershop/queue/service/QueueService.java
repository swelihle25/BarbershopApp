package com.barbershop.queue.service;
import com.barbershop.queue.entity.QueueEntry;
import com.barbershop.queue.entity.Customer;
import com.barbershop.queue.entity.Service;
import com.barbershop.queue.entity.Staff;
import com.barbershop.queue.enums.QueueStatus;
import com.barbershop.queue.repository.QueueEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Slf4j
public class QueueService {

    @Autowired
    private QueueEntryRepository queueEntryRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private StaffService staffService;

    public List<QueueEntry> getAllQueueEntries() {
        return queueEntryRepository.findAll();
    }

    public Optional<QueueEntry> getQueueEntryById(Long id) {
        return queueEntryRepository.findById(id);
    }

    public QueueEntry saveQueueEntry(QueueEntry queueEntry) {
        return queueEntryRepository.save(queueEntry);
    }

    public void deleteQueueEntry(Long id) {
        queueEntryRepository.deleteById(id);
    }

    // Real-Time Queue management methods
    public List<QueueEntry> getActiveQueue(String shopLocation) {
        return queueEntryRepository.findActiveQueueByLocation(shopLocation);
    }

    public List<QueueEntry> getTrulyActiveQueue(String shopLocation) {
        return queueEntryRepository.findTrulyActiveQueueByLocation(shopLocation);
    }

    public List<QueueEntry> getWaitingCustomers(String shopLocation) {
        return queueEntryRepository.findWaitingEntriesByLocation(shopLocation);
    }

    public Optional<QueueEntry> getNextWaitingCustomer(String shopLocation) {
        return queueEntryRepository.findNextWaitingCustomer(shopLocation);
    }

    @CacheEvict(value = "queue-stats", allEntries = true)
    @Transactional
    public QueueEntry addCustomerToQueue(Long customerId, Long serviceId, String shopLocation) {
        log.info("Adding customer {} to queue at {} and clearing queue stats cache", customerId, shopLocation);

        Customer customer = customerService.getCustomerById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        // Find service
        Service service = serviceService.getServiceById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + serviceId));

        // Check if customer is already in queue
        Optional<QueueEntry> existingEntry = queueEntryRepository
                .findByCustomerIdAndShopLocationAndStatus(customerId, shopLocation, QueueStatus.WAITING);

        if (existingEntry.isPresent()) {
            throw new RuntimeException("Customer is already in the queue");
        }

        // Get next queue position
        Integer maxPosition = queueEntryRepository.getMaxQueuePosition(shopLocation).orElse(0);
        int nextPosition = maxPosition + 1;

        // Create new queue entry
        QueueEntry queueEntry = QueueEntry.builder()
                .customer(customer)
                .service(service)
                .shopLocation(shopLocation)
                .queuePosition(nextPosition)
                .status(QueueStatus.WAITING)
                .joinedAt(LocalDateTime.now())
                .build();

        return queueEntryRepository.save(queueEntry);
    }

    @CacheEvict(value = "queue-stats", allEntries = true)
    @Transactional
    public QueueEntry startService(Long queueEntryId, Long staffId) {
        log.info("Starting service for queue entry {} and clearing queue stats cache", queueEntryId);

        // Find queue entry
        QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new RuntimeException("Queue entry not found with id: " + queueEntryId));

        // Find staff
        Staff staff = staffService.getStaffById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));

        // Check if queue entry is in waiting status
        if (queueEntry.getStatus() != QueueStatus.WAITING) {
            throw new RuntimeException("Queue entry is not in waiting status");
        }

        // Update queue entry
        queueEntry.setStatus(QueueStatus.IN_PROGRESS);
        queueEntry.setAssignedStaff(staff);
        queueEntry.setServiceStartedAt(LocalDateTime.now());

        return queueEntryRepository.save(queueEntry);
    }

    @CacheEvict(value = "queue-stats", allEntries = true)
    @Transactional
    public QueueEntry completeService(Long queueEntryId) {
        log.info("Completing service for queue entry {} and clearing queue stats cache", queueEntryId);

        // Find queue entry
        QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new RuntimeException("Queue entry not found with id: " + queueEntryId));

        // Check if service is in progress
        if (queueEntry.getStatus() != QueueStatus.IN_PROGRESS) {
            throw new RuntimeException("Service is not in progress");
        }

        // Complete the service
        queueEntry.setStatus(QueueStatus.COMPLETED);
        queueEntry.setServiceCompletedAt(LocalDateTime.now());

        return queueEntryRepository.save(queueEntry);
    }

    @CacheEvict(value = "queue-stats", allEntries = true)
    @Transactional
    public void removeFromQueue(Long queueEntryId) {
        log.info("Removing queue entry {} and clearing queue stats cache", queueEntryId);
        // Check if queue entry exists
        QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new RuntimeException("Queue entry not found with id: " + queueEntryId));

        queueEntryRepository.deleteById(queueEntryId);
    }


    @Cacheable(value = "queue-stats", key = "'waiting_count_' + #shopLocation")
    public Integer getWaitingCustomersCount(String shopLocation) {
        log.info("Fetching waiting customers count for location: {}", shopLocation);
        return queueEntryRepository.countWaitingCustomers(shopLocation);
    }


    public Integer getCustomersAheadCount(String shopLocation, Integer position) {
        return queueEntryRepository.countCustomersAhead(shopLocation, position);
    }

    public Optional<QueueEntry> findCustomerInQueue(Long customerId, String shopLocation) {
        return queueEntryRepository.findByCustomerIdAndShopLocationAndStatus(
                customerId, shopLocation, QueueStatus.WAITING);
    }

    // Staff related methods
    public List<QueueEntry> getCurrentServicesByStaff(Long staffId) {
        return queueEntryRepository.findCurrentServicesByStaff(staffId);
    }

    // Reporting methods
    @Cacheable(value = "queue-stats", key = "'served_today_' + #shopLocation")
    public Integer getCustomersServedToday(String shopLocation) {
        log.info("Fetching customers served today for location: {}", shopLocation);
        LocalDate today = LocalDate.now();
        return queueEntryRepository.countCustomersServedByDate(shopLocation, today);
    }

    @Cacheable(value = "queue-stats", key = "'served_' + #shopLocation + '_' + #date")
    public Integer getCustomersServedByDate(String shopLocation, LocalDate date) {
        log.info("Fetching customers served on {} for location: {}", date, shopLocation);
        return queueEntryRepository.countCustomersServedByDate(shopLocation, date);
    }


    public Double getAverageWaitTimeToday(String shopLocation) {
        LocalDate today = LocalDate.now();
        return queueEntryRepository.getAverageWaitTimeByDate(shopLocation, today);
    }

    public Double getAverageWaitTimeByDate(String shopLocation, LocalDate date) {
        return queueEntryRepository.getAverageWaitTimeByDate(shopLocation, date);
    }

    public List<Object[]> getPopularServicesStats(String shopLocation, LocalDate date) {
//        return queueEntryRepository.getPopularServicesByDate(shopLocation, date);
        return null;
    }

    public List<Object[]> getStaffPerformanceStats(String shopLocation, LocalDate date) {
//        return queueEntryRepository.getStaffPerformanceByDate(shopLocation, date);
        return null;
    }

    // Helper method to get queue position for customer
    public Integer getCustomerQueuePosition(Long customerId, String shopLocation) {
        Optional<QueueEntry> queueEntry = findCustomerInQueue(customerId, shopLocation);
        if (queueEntry.isPresent()) {
            return queueEntry.get().getQueuePosition();
        }
        return null;
    }

    // Helper method to get estimated wait time
    public Integer getEstimatedWaitTime(String shopLocation, Integer position) {
        // Simple calculation: average 30 minutes per customer
        Integer customersAhead = getCustomersAheadCount(shopLocation, position);
        return customersAhead * 30; // 30 minutes per customer
    }
}