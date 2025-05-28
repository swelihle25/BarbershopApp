package com.barbershop.queue.exception;

public class QueueEntryNotFound extends RuntimeException {
    public QueueEntryNotFound(String message) {
        super(message);
    }
}
