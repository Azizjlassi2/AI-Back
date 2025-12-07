package com.aiplus.backend.datasets.exception;

/**
 * Exception thrown when a dataset is not found
 * 
 * 
 */
public class DatasetNotFoundException extends RuntimeException {

    public DatasetNotFoundException(String message) {
        super(message);
    }

    public DatasetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
