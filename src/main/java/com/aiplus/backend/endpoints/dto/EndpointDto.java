package com.aiplus.backend.endpoints.dto;

import org.springframework.data.annotation.ReadOnlyProperty;

import lombok.Data;

/**
 * DTO for API Endpoint of a model.
 */
@Data
public class EndpointDto {
    @ReadOnlyProperty
    private Long id;
    private String method;
    private String path;
    private String description;
    private String requestBody;
    private String successResponse;
    private String errorResponse;

}