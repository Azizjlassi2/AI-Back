package com.aiplus.backend.subscription.dto;

import java.time.LocalDate;

import com.aiplus.backend.payment.dto.PaymentSummaryDTO;
import com.aiplus.backend.subscription.model.SubscriptionStatus;
import com.aiplus.backend.subscriptionPlans.dto.SubscriptionPlanDto;
import com.aiplus.backend.users.dto.ClientAccountDto;
import com.aiplus.backend.users.dto.DeveloperAccountDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionDetailsDTO {

    private Long id;

    private ClientAccountDto client;

    private SubscriptionPlanDto plan;

    private DeveloperAccountDto developer;

    private PaymentSummaryDTO payment;

    private LocalDate startDate;

    private SubscriptionStatus status;

    private LocalDate nextBillingDate;

    private boolean recurring;

}
