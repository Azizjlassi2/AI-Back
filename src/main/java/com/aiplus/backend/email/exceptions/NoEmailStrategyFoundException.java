package com.aiplus.backend.email.exceptions;

public class NoEmailStrategyFoundException extends RuntimeException {
    public NoEmailStrategyFoundException(String message) {
        super(message);
    }
}