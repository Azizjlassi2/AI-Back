package com.aiplus.backend.models.dto;

import java.util.List;

import com.aiplus.backend.endpoints.dto.EndpointDto;
import com.aiplus.backend.models.model.Visibility;
import com.aiplus.backend.subscriptionPlans.dto.SubscriptionPlanDto;

import lombok.Data;

@Data
public class AiModelCreateDto {

    private String name;
    private String description;

    private String image; // docker image URL

    private Visibility visibility;
    private String framework;
    private String architecture;
    private String trainingDataset;

    private PerformanceMetricsDto performance;
    private List<TaskDto> tasks;
    private List<EndpointDto> endpoints;
    private List<SubscriptionPlanDto> subscriptionPlans;

}
