package com.aiplus.backend.models.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.ReadOnlyProperty;

import com.aiplus.backend.comments.dto.ModelCommentDto;
import com.aiplus.backend.endpoints.dto.EndpointDto;
import com.aiplus.backend.models.model.Visibility;
import com.aiplus.backend.subscriptions.dto.SubscriptionPlanDto;
import com.aiplus.backend.users.dto.UserSummaryDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiModelDto {

    @NotEmpty
    @NotNull
    private Long id;

    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private String description;

    private Visibility visibility;

    private String framework;
    private String architecture;
    private String trainingDataset;

    @NotEmpty
    @NotNull
    private UserSummaryDto developer;

    private PerformanceMetricsDto performance;

    @ReadOnlyProperty
    private ModelStatsDto stats;

    @NotEmpty
    @NotNull
    private List<TaskDto> tasks;

    @NotEmpty
    @NotNull
    private List<EndpointDto> endpoints;
    @NotEmpty
    @NotNull
    private List<SubscriptionPlanDto> subscriptionPlans;

    @ReadOnlyProperty
    private List<ModelCommentDto> comments;

    @ReadOnlyProperty
    private LocalDate createdAt;

}
