package com.aiplus.backend.subscriptions.dto;

import java.util.List;

import com.aiplus.backend.subscriptions.model.BillingPeriod;

import lombok.Data;

/**
 * DTO for subscription plans of a model.
 */
@Data
public class SubscriptionPlanDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String currency;
    private BillingPeriod billingPeriod;
    private List<String> features;
    private Integer apiCallsLimit;
    private Double apiCallsPrice;
}