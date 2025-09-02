package com.aiplus.backend.models.dto;

import java.time.LocalDate;
import java.util.List;

import com.aiplus.backend.users.dto.UserSummaryDto;

import lombok.Data;

@Data
public class AiModelSummaryDto {
    private Long id;
    private String name;
    private String description;

    private UserSummaryDto developer;
    private List<TaskDto> tasks;
    private ModelStatsDto stats;

    private LocalDate createdAt;

}
