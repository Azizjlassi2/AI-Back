package com.aiplus.backend.models.exceptions;

public class DeveloperNotFoundException extends RuntimeException {
    public DeveloperNotFoundException(Long developerId) {
        super("Developer not found with id: " + developerId);
    }

    public DeveloperNotFoundException(String email) {
        super("Developer not found with email: " + email);
    }

    public DeveloperNotFoundException(String username, String email) {
        super("Developer not found with username: " + username + " and email: " + email);
    }

}
