package com.aiplus.backend.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDto {
    private Long id;
    private String name;

}
