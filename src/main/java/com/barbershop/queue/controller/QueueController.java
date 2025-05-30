package com.barbershop.queue.controller;

import com.barbershop.queue.entity.QueueEntry;
import com.barbershop.queue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/queue")
@CrossOrigin(origins = "*")
public class QueueController {

    @Autowired
    private QueueService queueService;

    // Get all queue entries
    @GetMapping
    public ResponseEntity<List<QueueEntry>> getAllQueueEntries() {
        List<QueueEntry> entries = queueService.getAllQueueEntries();
        return ResponseEntity.ok(entries);
    }

    // Get queue entry by ID
    @GetMapping("/{id}")
    public ResponseEntity<QueueEntry> getQueueEntryById(@PathVariable Long id) {
        Optional<QueueEntry> entry = queueService.getQueueEntryById(id);
        if (entry.isPresent()) {
            return ResponseEntity.ok(entry.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Get active queue for a location
    @GetMapping("/active/{location}")
    public ResponseEntity<List<QueueEntry>> getActiveQueue(@PathVariable String location) {
        List<QueueEntry> activeQueue = queueService.getActiveQueue(location);
        return ResponseEntity.ok(activeQueue);
    }

    // Get waiting customers for a location
    @GetMapping("/waiting/{location}")
    public ResponseEntity<List<QueueEntry>> getWaitingCustomers(@PathVariable String location) {
        List<QueueEntry> waitingCustomers = queueService.getWaitingCustomers(location);
        return ResponseEntity.ok(waitingCustomers);
    }

    // Get next waiting customer
    @GetMapping("/next/{location}")
    public ResponseEntity<QueueEntry> getNextWaitingCustomer(@PathVariable String location) {
        Optional<QueueEntry> nextCustomer = queueService.getNextWaitingCustomer(location);
        if (nextCustomer.isPresent()) {
            return ResponseEntity.ok(nextCustomer.get());
        }
        return ResponseEntity.notFound().build();
    }
    // Get truly active queue for a location (only waiting and in progress)
    @GetMapping("/truly-active/{location}")
    public ResponseEntity<List<QueueEntry>> getTrulyActiveQueue(@PathVariable String location) {
        List<QueueEntry> activeQueue = queueService.getTrulyActiveQueue(location);
        return ResponseEntity.ok(activeQueue);
    }


    // Add customer to queue
    @PostMapping("/add")
    public ResponseEntity<QueueEntry> addToQueue(@RequestParam Long customerId,
                                                 @RequestParam Long serviceId,
                                                 @RequestParam String shopLocation) {
        try {
            QueueEntry queueEntry = queueService.addCustomerToQueue(customerId, serviceId, shopLocation);
            return ResponseEntity.ok(queueEntry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Start service
    @PutMapping("/{id}/start")
    public ResponseEntity<QueueEntry> startService(@PathVariable Long id, @RequestParam Long staffId) {
        try {
            QueueEntry queueEntry = queueService.startService(id, staffId);
            return ResponseEntity.ok(queueEntry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Complete service
    @PutMapping("/{id}/complete")
    public ResponseEntity<QueueEntry> completeService(@PathVariable Long id) {
        try {
            QueueEntry queueEntry = queueService.completeService(id);
            return ResponseEntity.ok(queueEntry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Remove from queue
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeFromQueue(@PathVariable Long id) {
        try {
            queueService.removeFromQueue(id);
            return ResponseEntity.ok("Queue entry removed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get waiting customers count
    @GetMapping("/count/waiting/{location}")
    public ResponseEntity<Integer> getWaitingCount(@PathVariable String location) {
        Integer count = queueService.getWaitingCustomersCount(location);
        return ResponseEntity.ok(count);
    }

    // Get customers ahead count
    @GetMapping("/count/ahead/{location}")
    public ResponseEntity<Integer> getCustomersAhead(@PathVariable String location,
                                                     @RequestParam Integer position) {
        Integer count = queueService.getCustomersAheadCount(location, position);
        return ResponseEntity.ok(count);
    }

    // Find customer in queue
    @GetMapping("/customer/{customerId}/location/{location}")
    public ResponseEntity<QueueEntry> findCustomerInQueue(@PathVariable Long customerId,
                                                          @PathVariable String location) {
        Optional<QueueEntry> entry = queueService.findCustomerInQueue(customerId, location);
        if (entry.isPresent()) {
            return ResponseEntity.ok(entry.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Get customer queue position
    @GetMapping("/position/customer/{customerId}/location/{location}")
    public ResponseEntity<Integer> getCustomerPosition(@PathVariable Long customerId,
                                                       @PathVariable String location) {
        Integer position = queueService.getCustomerQueuePosition(customerId, location);
        if (position != null) {
            return ResponseEntity.ok(position);
        }
        return ResponseEntity.notFound().build();
    }

    // Get estimated wait time
    @GetMapping("/wait-time/{location}")
    public ResponseEntity<Integer> getEstimatedWaitTime(@PathVariable String location,
                                                        @RequestParam Integer position) {
        Integer waitTime = queueService.getEstimatedWaitTime(location, position);
        return ResponseEntity.ok(waitTime);
    }

    // Get current services by staff
    @GetMapping("/staff/{staffId}/current")
    public ResponseEntity<List<QueueEntry>> getCurrentServicesByStaff(@PathVariable Long staffId) {
        List<QueueEntry> services = queueService.getCurrentServicesByStaff(staffId);
        return ResponseEntity.ok(services);
    }

    // Reporting endpoints
    @GetMapping("/report/customers-served/today/{location}")
    public ResponseEntity<Integer> getCustomersServedToday(@PathVariable String location) {
        Integer count = queueService.getCustomersServedToday(location);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/report/customers-served/{location}")
    public ResponseEntity<Integer> getCustomersServedByDate(@PathVariable String location,
                                                            @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            Integer count = queueService.getCustomersServedByDate(location, localDate);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/report/wait-time/today/{location}")
    public ResponseEntity<Double> getAverageWaitTimeToday(@PathVariable String location) {
        Double waitTime = queueService.getAverageWaitTimeToday(location);
        return ResponseEntity.ok(waitTime != null ? waitTime : 0.0);
    }

    @GetMapping("/report/wait-time/{location}")
    public ResponseEntity<Double> getAverageWaitTimeByDate(@PathVariable String location,
                                                           @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            Double waitTime = queueService.getAverageWaitTimeByDate(location, localDate);
            return ResponseEntity.ok(waitTime != null ? waitTime : 0.0);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/report/popular-services/{location}")
    public ResponseEntity<List<Object[]>> getPopularServices(@PathVariable String location,
                                                             @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<Object[]> stats = queueService.getPopularServicesStats(location, localDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/report/staff-performance/{location}")
    public ResponseEntity<List<Object[]>> getStaffPerformance(@PathVariable String location,
                                                              @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<Object[]> stats = queueService.getStaffPerformanceStats(location, localDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}