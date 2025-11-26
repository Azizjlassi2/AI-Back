package com.aiplus.backend.models.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for Model Statistics. Contains statistics related to the
 * AI model's usage and popularity.
 */
@Data
@Builder
public class ModelStatsDto {
    private long used;
    private int stars;
    private int discussions;
}
