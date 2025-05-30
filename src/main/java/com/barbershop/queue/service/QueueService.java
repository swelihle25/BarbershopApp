package com.barbershop.queue.service;
import com.barbershop.queue.entity.QueueEntry;
import com.barbershop.queue.entity.Customer;
import com.barbershop.queue.entity.Service;
import com.barbershop.queue.entity.Staff;
import com.barbershop.queue.enums.QueueStatus;
import com.barbershop.queue.repository.QueueEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
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

    // Queue management methods
    public List<QueueEntry> getActiveQueue(String shopLocation) {
        return queueEntryRepository.findActiveQueueByLocation(shopLocation);
    }

    // Get only truly active queue (waiting + in progress)
    public List<QueueEntry> getTrulyActiveQueue(String shopLocation) {
        return queueEntryRepository.findTrulyActiveQueueByLocation(shopLocation);
    }



    public List<QueueEntry> getWaitingCustomers(String shopLocation) {
        return queueEntryRepository.findWaitingEntriesByLocation(shopLocation);
    }

    public Optional<QueueEntry> getNextWaitingCustomer(String shopLocation) {
        return queueEntryRepository.findNextWaitingCustomer(shopLocation);
    }


    public QueueEntry addCustomerToQueue(Long customerId, Long serviceId, String shopLocation) {
        // Find customer
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

    public QueueEntry startService(Long queueEntryId, Long staffId) {
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

    public QueueEntry completeService(Long queueEntryId) {
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

    public void removeFromQueue(Long queueEntryId) {
        // Check if queue entry exists
        QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new RuntimeException("Queue entry not found with id: " + queueEntryId));

        queueEntryRepository.deleteById(queueEntryId);
    }

    // Queue information methods
    public Integer getWaitingCustomersCount(String shopLocation) {
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
    public Integer getCustomersServedToday(String shopLocation) {
        LocalDate today = LocalDate.now();
        return queueEntryRepository.countCustomersServedByDate(shopLocation, today);
    }

    public Integer getCustomersServedByDate(String shopLocation, LocalDate date) {
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