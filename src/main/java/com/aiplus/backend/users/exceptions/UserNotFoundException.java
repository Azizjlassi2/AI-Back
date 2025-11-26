package com.aiplus.backend.users.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long id) {
        super("User with ID " + id
                + " was not found in the system. Please verify the identifier or ensure the user exists before performing this operation.");
    }

}
