package com.aiplus.backend.auth.exceptions;

/**
 * Exception thrown when an invalid role is specified during user registration
 * or role assignment.
 */
public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String role) {
        super("Invalid role: " + role);
    }
}
