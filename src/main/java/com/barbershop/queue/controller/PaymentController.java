package com.barbershop.queue.controller;

import com.barbershop.queue.entity.Payment;
import com.barbershop.queue.enums.PaymentMethod;
import com.barbershop.queue.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Get all payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        if (payment.isPresent()) {
            return ResponseEntity.ok(payment.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Process payment (complete immediately)
    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(
            @RequestParam Long queueEntryId,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam BigDecimal amount) {
        try {
            Payment payment = paymentService.processPayment(queueEntryId, paymentMethod, amount);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Create pending payment
    @PostMapping("/create-pending")
    public ResponseEntity<Payment> createPendingPayment(
            @RequestParam Long queueEntryId,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam BigDecimal amount) {
        try {
            Payment payment = paymentService.createPendingPayment(queueEntryId, paymentMethod, amount);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Complete pending payment
    @PutMapping("/{id}/complete")
    public ResponseEntity<Payment> completePayment(@PathVariable Long id) {
        try {
            Payment payment = paymentService.completePayment(id);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cancel payment
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Payment> cancelPayment(@PathVariable Long id) {
        try {
            Payment payment = paymentService.cancelPayment(id);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get payments by queue entry
    @GetMapping("/queue-entry/{queueEntryId}")
    public ResponseEntity<List<Payment>> getPaymentsByQueueEntry(@PathVariable Long queueEntryId) {
        List<Payment> payments = paymentService.getPaymentsByQueueEntry(queueEntryId);
        return ResponseEntity.ok(payments);
    }

    // Get payments by location and date
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Payment>> getPaymentsByLocationAndDate(
            @PathVariable String location,
            @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<Payment> payments = paymentService.getPaymentsByLocationAndDate(location, localDate);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get today's payments for a location
    @GetMapping("/today/{location}")
    public ResponseEntity<List<Payment>> getTodayPayments(@PathVariable String location) {
        List<Payment> payments = paymentService.getTodayPayments(location);
        return ResponseEntity.ok(payments);
    }

    // Get pending payments for a location
    @GetMapping("/pending/{location}")
    public ResponseEntity<List<Payment>> getPendingPayments(@PathVariable String location) {
        List<Payment> payments = paymentService.getPendingPayments(location);
        return ResponseEntity.ok(payments);
    }

    // Revenue endpoints
    @GetMapping("/revenue/today/{location}")
    public ResponseEntity<BigDecimal> getTodayTotalRevenue(@PathVariable String location) {
        BigDecimal revenue = paymentService.getTodayTotalRevenue(location);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue/{location}")
    public ResponseEntity<BigDecimal> getTotalRevenueByDate(
            @PathVariable String location,
            @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            BigDecimal revenue = paymentService.getTotalRevenueByDate(location, localDate);
            return ResponseEntity.ok(revenue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/revenue/monthly/{location}")
    public ResponseEntity<BigDecimal> getMonthlyRevenue(
            @PathVariable String location,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        BigDecimal revenue = paymentService.getMonthlyRevenue(location, year, month);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue/current-month/{location}")
    public ResponseEntity<BigDecimal> getCurrentMonthRevenue(@PathVariable String location) {
        BigDecimal revenue = paymentService.getCurrentMonthRevenue(location);
        return ResponseEntity.ok(revenue);
    }

    // Payment method statistics
    @GetMapping("/count/method/{location}")
    public ResponseEntity<Integer> countPaymentsByMethod(
            @PathVariable String location,
            @RequestParam PaymentMethod method,
            @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            Integer count = paymentService.countPaymentsByMethod(location, method, localDate);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/count/method/today/{location}")
    public ResponseEntity<Integer> countTodayPaymentsByMethod(
            @PathVariable String location,
            @RequestParam PaymentMethod method) {
        Integer count = paymentService.countTodayPaymentsByMethod(location, method);
        return ResponseEntity.ok(count);
    }

    // Statistics endpoints
    @GetMapping("/stats/total-count/{location}")
    public ResponseEntity<Integer> getTotalPaymentsCount(
            @PathVariable String location,
            @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            Integer count = paymentService.getTotalPaymentsCount(location, localDate);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats/average-amount/{location}")
    public ResponseEntity<BigDecimal> getAveragePaymentAmount(
            @PathVariable String location,
            @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            BigDecimal average = paymentService.getAveragePaymentAmount(location, localDate);
            return ResponseEntity.ok(average);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Check if queue entry is paid
    @GetMapping("/check-paid/{queueEntryId}")
    public ResponseEntity<Boolean> isQueueEntryPaid(@PathVariable Long queueEntryId) {
        boolean isPaid = paymentService.isQueueEntryPaid(queueEntryId);
        return ResponseEntity.ok(isPaid);
    }

    // Delete payment
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.ok("Payment deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
