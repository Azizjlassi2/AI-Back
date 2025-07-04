package com.aiplus.backend.email.exceptions;

public class ContactMessageEmailException extends RuntimeException {
    public ContactMessageEmailException(String message) {
        super(message);
    }

    public ContactMessageEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
