package com.barbershop.queue.repository;

import com.barbershop.queue.entity.Payment;
import com.barbershop.queue.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByQueueEntryId(Long queueEntryId);

    @Query("SELECT p FROM Payment p JOIN p.queueEntry q WHERE q.shopLocation = :location " +
            "AND DATE(p.paidAt) = :date ORDER BY p.paidAt DESC")
    List<Payment> findPaymentsByLocationAndDate(@Param("location") String location, @Param("date") LocalDate date);

    @Query("SELECT SUM(p.amount) FROM Payment p JOIN p.queueEntry q WHERE q.shopLocation = :location " +
            "AND DATE(p.paidAt) = :date AND p.status = 'COMPLETED'")
    BigDecimal getTotalRevenueByDate(@Param("location") String location, @Param("date") LocalDate date);

    @Query("SELECT COUNT(p) FROM Payment p JOIN p.queueEntry q WHERE q.shopLocation = :location " +
            "AND p.method = :method AND DATE(p.paidAt) = :date")
    Integer countPaymentsByMethodAndDate(@Param("location") String location,
                                         @Param("method") PaymentMethod method,
                                         @Param("date") LocalDate date);

    // Monthly revenue query
    @Query("SELECT SUM(p.amount) FROM Payment p JOIN p.queueEntry q WHERE q.shopLocation = :location " +
            "AND YEAR(p.paidAt) = :year AND MONTH(p.paidAt) = :month AND p.status = 'COMPLETED'")
    BigDecimal getMonthlyRevenue(@Param("location") String location,
                                 @Param("year") Integer year,
                                 @Param("month") Integer month);
}
