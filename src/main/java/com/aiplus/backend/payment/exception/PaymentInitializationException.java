package com.aiplus.backend.payment.exception;

public class PaymentInitializationException extends RuntimeException {
    public PaymentInitializationException(String message) {
        super(message);
    }

    public PaymentInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
