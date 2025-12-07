package com.aiplus.backend.datasets.dto;

import com.aiplus.backend.datasets.model.Visibility;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatasetResponse {
    private Long id;
    private String name;
    private String description;
    private Visibility visibility;
    private String createdAt;
    private String updatedAt;
    private Long developerAccountId;
    private String size;
    private String format;
    private String license;
    private String purchasePlanName;

}
