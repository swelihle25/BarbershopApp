package com.barbershop.queue.exception;

public class NoCustomerWaitingException extends RuntimeException {
    public NoCustomerWaitingException(String message) {
        super(message);
    }
}
