package com.aiplus.backend.datasets.dto;

import java.util.List;

import com.aiplus.backend.datasets.model.BillingPeriod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchasePlanDto {
    @NotBlank
    private String name;

    private String description;

    @Builder.Default
    @DecimalMin("0.0")
    private Double price = 0.0;

    @Builder.Default
    @NotNull
    private String currency = "TND";

    @Builder.Default
    @NotNull
    private BillingPeriod billingPeriod = BillingPeriod.ONE_TIME;

    @Builder.Default
    private List<@NotBlank String> features = List.of();

}
