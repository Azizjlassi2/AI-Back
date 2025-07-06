package com.aiplus.backend.utils.responses;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
/**
 * Represents a generic API response.
 *
 * @param <T> the type of data contained in the response
 */
public class ApiResponse<T> {

    private LocalDateTime timestamp = LocalDateTime.now();
    private boolean success;
    private String message;
    private T data;
    private Map<String, Object> metadata;

    public ApiResponse(boolean success, String message, T data, Map<String, Object> metadata) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.metadata = metadata;
    }

    // Getters and setters...
}
