package com.aiplus.backend.datasets.dto;

import com.aiplus.backend.models.model.Visibility;

import lombok.Data;

@Data
public class DatasetUpdateDTO {
    private String name;
    private String description;
    private Visibility visibility;

}
