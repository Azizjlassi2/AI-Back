package com.aiplus.backend.models.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PerformanceMetrics {
    private String accuracyScore;
    private String precisionScore;
    private String recallScore;
    private String f1Score;
}