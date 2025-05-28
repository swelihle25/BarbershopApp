package com.barbershop.queue.service;
import com.barbershop.queue.entity.Payment;
import com.barbershop.queue.entity.QueueEntry;
import com.barbershop.queue.enums.PaymentMethod;
import com.barbershop.queue.enums.PaymentStatus;
import com.barbershop.queue.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private QueueService queueService;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    // Process payment for a queue entry
    public Payment processPayment(Long queueEntryId, PaymentMethod paymentMethod, BigDecimal amount) {
        QueueEntry queueEntry = queueService.getQueueEntryById(queueEntryId)
                .orElseThrow(() -> new RuntimeException("Queue entry not found with id: " + queueEntryId));

        // Check if payment already exists for this queue entry
        List<Payment> existingPayments = getPaymentsByQueueEntry(queueEntryId);
        if (!existingPayments.isEmpty()) {
            throw new RuntimeException("Payment already exists for this queue entry");
        }

        Payment payment = Payment.builder()
                .queueEntry(queueEntry)
                .amount(amount)
                .method(paymentMethod)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    // Create pending payment
    public Payment createPendingPayment(Long queueEntryId, PaymentMethod paymentMethod, BigDecimal amount) {
        QueueEntry queueEntry = queueService.getQueueEntryById(queueEntryId)
                .orElseThrow(() -> new RuntimeException("Queue entry not found with id: " + queueEntryId));

        Payment payment = Payment.builder()
                .queueEntry(queueEntry)
                .amount(amount)
                .method(paymentMethod)
                .status(PaymentStatus.PENDING)
                .paidAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    // Complete pending payment
    public Payment completePayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not in pending status");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    // Cancel payment
    public Payment cancelPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        payment.setStatus(PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }

    // Get payments by queue entry
    public List<Payment> getPaymentsByQueueEntry(Long queueEntryId) {
        return paymentRepository.findByQueueEntryId(queueEntryId);
    }

    // Get payments by location and date
    public List<Payment> getPaymentsByLocationAndDate(String location, LocalDate date) {
        return paymentRepository.findPaymentsByLocationAndDate(location, date);
    }

    // Get today's payments for a location
    public List<Payment> getTodayPayments(String location) {
        LocalDate today = LocalDate.now();
        return paymentRepository.findPaymentsByLocationAndDate(location, today);
    }

    // Revenue methods
    public BigDecimal getTotalRevenueByDate(String location, LocalDate date) {
        BigDecimal revenue = paymentRepository.getTotalRevenueByDate(location, date);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public BigDecimal getTodayTotalRevenue(String location) {
        LocalDate today = LocalDate.now();
        return getTotalRevenueByDate(location, today);
    }

    public BigDecimal getMonthlyRevenue(String location, Integer year, Integer month) {
        BigDecimal revenue = paymentRepository.getMonthlyRevenue(location, year, month);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public BigDecimal getCurrentMonthRevenue(String location) {
        LocalDate now = LocalDate.now();
        return getMonthlyRevenue(location, now.getYear(), now.getMonthValue());
    }

    // Payment method statistics
    public Integer countPaymentsByMethod(String location, PaymentMethod method, LocalDate date) {
        return paymentRepository.countPaymentsByMethodAndDate(location, method, date);
    }

    public Integer countTodayPaymentsByMethod(String location, PaymentMethod method) {
        LocalDate today = LocalDate.now();
        return paymentRepository.countPaymentsByMethodAndDate(location, method, today);
    }

    // Helper methods for payment statistics
    public Integer getTotalPaymentsCount(String location, LocalDate date) {
        List<Payment> payments = getPaymentsByLocationAndDate(location, date);
        return payments.size();
    }

    public BigDecimal getAveragePaymentAmount(String location, LocalDate date) {
        List<Payment> payments = getPaymentsByLocationAndDate(location, date);
        if (payments.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                total = total.add(payment.getAmount());
            }
        }

        return total.divide(BigDecimal.valueOf(payments.size()));
    }

    // Check if queue entry has been paid
    public boolean isQueueEntryPaid(Long queueEntryId) {
        List<Payment> payments = getPaymentsByQueueEntry(queueEntryId);
        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                return true;
            }
        }
        return false;
    }

    // Get pending payments for a location
    public List<Payment> getPendingPayments(String location) {
        List<Payment> allPayments = getTodayPayments(location);
        List<Payment> pendingPayments = allPayments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.PENDING)
                .toList();
        return pendingPayments;
    }
}