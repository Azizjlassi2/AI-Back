package com.aiplus.backend.email.exceptions;

public class AccountActivationEmailException extends RuntimeException {
    public AccountActivationEmailException(String message) {
        super(message);
    }

    public AccountActivationEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
