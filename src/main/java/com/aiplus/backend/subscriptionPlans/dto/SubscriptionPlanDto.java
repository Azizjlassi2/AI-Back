package com.aiplus.backend.subscriptionPlans.dto;

import java.util.List;

import org.springframework.data.annotation.ReadOnlyProperty;

import com.aiplus.backend.models.dto.AiModelShortSummaryDto;
import com.aiplus.backend.subscriptionPlans.model.BillingPeriod;

import lombok.Data;

/**
 * DTO for subscription plans of a model.
 */
@Data
public class SubscriptionPlanDto {

    @ReadOnlyProperty
    private Long id;
    private String name;
    private String description;
    private double price;
    private String currency;
    private BillingPeriod billingPeriod;
    private List<String> features;
    private Integer apiCallsLimit;
    private AiModelShortSummaryDto model;
}