package com.aiplus.backend.models.dto;

import lombok.Data;

/**
 * Data Transfer Object for Performance Metrics.
 * Contains performance metrics related to the AI model.
 */
@Data
public class PerformanceMetricsDto {
    private String accuracyScore;
    private String precisionScore;
    private String recallScore;
    private String f1Score;
}