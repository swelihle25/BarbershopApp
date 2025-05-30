package com.barbershop.queue.repository;

import com.barbershop.queue.entity.QueueEntry;
import com.barbershop.queue.enums.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {
    // Find current active queue for a location
    @Query("SELECT q FROM QueueEntry q WHERE q.shopLocation = :location " +
            "AND q.status IN ('WAITING', 'IN_PROGRESS', 'COMPLETED') ORDER BY q.queuePosition")
    List<QueueEntry> findActiveQueueByLocation(@Param("location") String location);

    // Find truly active queue (only waiting and in progress)
    @Query("SELECT q FROM QueueEntry q WHERE q.shopLocation = :location " +
            "AND q.status IN ('WAITING', 'IN_PROGRESS') ORDER BY q.queuePosition")
    List<QueueEntry> findTrulyActiveQueueByLocation(@Param("location") String location);
    // Find only waiting customers
    @Query("SELECT q FROM QueueEntry q WHERE q.shopLocation = :location " +
            "AND q.status = 'WAITING' ORDER BY q.queuePosition")
    List<QueueEntry> findWaitingEntriesByLocation(@Param("location") String location);

    // Find next waiting customer
    @Query("SELECT q FROM QueueEntry q WHERE q.shopLocation = :location " +
            "AND q.status = 'WAITING' ORDER BY q.queuePosition LIMIT 1")
    Optional<QueueEntry> findNextWaitingCustomer(@Param("location") String location);

    // Find customer's current queue entry
    Optional<QueueEntry> findByCustomerIdAndShopLocationAndStatus(Long customerId,
                                                                  String shopLocation,
                                                                  QueueStatus status);

    // Get maximum queue position for location
    @Query("SELECT MAX(q.queuePosition) FROM QueueEntry q WHERE q.shopLocation = :location " +
            "AND q.status IN ('WAITING', 'IN_PROGRESS')")
    Optional<Integer> getMaxQueuePosition(@Param("location") String location);

    // Count waiting customers
    @Query("SELECT COUNT(q) FROM QueueEntry q WHERE q.shopLocation = :location AND q.status = 'WAITING'")
    Integer countWaitingCustomers(@Param("location") String location);

    // Count customers ahead in queue
    @Query("SELECT COUNT(q) FROM QueueEntry q WHERE q.shopLocation = :location " +
            "AND q.status = 'WAITING' AND q.queuePosition < :position")
    Integer countCustomersAhead(@Param("location") String location, @Param("position") Integer position);

    // Daily reporting queries
    @Query("SELECT COUNT(q) FROM QueueEntry q WHERE q.shopLocation = :location " +
            "AND DATE(q.serviceCompletedAt) = :date AND q.status = 'COMPLETED'")
    Integer countCustomersServedByDate(@Param("location") String location, @Param("date") LocalDate date);

    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, q.joinedAt, q.serviceStartedAt)) FROM QueueEntry q " +
            "WHERE q.shopLocation = :location AND DATE(q.joinedAt) = :date AND q.serviceStartedAt IS NOT NULL")
    Double getAverageWaitTimeByDate(@Param("location") String location, @Param("date") LocalDate date);

    // Find entries by staff member
    @Query("SELECT q FROM QueueEntry q WHERE q.assignedStaff.id = :staffId AND q.status = 'IN_PROGRESS'")
    List<QueueEntry> findCurrentServicesByStaff(@Param("staffId") Long staffId);



}
