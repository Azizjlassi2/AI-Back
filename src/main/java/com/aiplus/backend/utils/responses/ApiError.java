package com.aiplus.backend.utils.responses;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents an API error response.
 */
@AllArgsConstructor
@Data
public class ApiError {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String message;
    private List<String> details;

}