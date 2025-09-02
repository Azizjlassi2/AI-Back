package com.aiplus.backend.models.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ModelStats {
    private long used;
    private int stars;
    private int discussions;
}