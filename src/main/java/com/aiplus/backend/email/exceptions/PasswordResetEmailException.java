package com.aiplus.backend.email.exceptions;

public class PasswordResetEmailException extends RuntimeException {
    public PasswordResetEmailException(String message) {
        super(message);
    }

    public PasswordResetEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}