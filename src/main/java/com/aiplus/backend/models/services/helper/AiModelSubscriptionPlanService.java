package com.aiplus.backend.models.services.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.subscriptions.dto.SubscriptionPlanDto;
import com.aiplus.backend.subscriptions.mapper.SubscriptionPlanMapper;
import com.aiplus.backend.subscriptions.model.SubscriptionPlan;
import com.aiplus.backend.subscriptions.repository.SubscriptionPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiModelSubscriptionPlanService {

    private final SubscriptionPlanMapper spMapper;
    private final SubscriptionPlanRepository spRepository;

    public List<SubscriptionPlan> attachSubscriptionPlans(List<SubscriptionPlanDto> dtoList, AiModel model) {
        List<SubscriptionPlan> etendue;
        try {
            etendue = new ArrayList<>();
            if (dtoList != null) {
                dtoList.forEach(dto -> {
                    SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
                    subscriptionPlan.setName(dto.getName());
                    subscriptionPlan.setDescription(dto.getDescription());
                    subscriptionPlan.setPrice(dto.getPrice());
                    subscriptionPlan.setFeatures(dto.getFeatures());
                    subscriptionPlan.setApiCallsLimit(dto.getApiCallsLimit());
                    subscriptionPlan.setModel(model);
                    etendue.add(spRepository.save(subscriptionPlan));
                });
            }
            return etendue;

        } catch (Exception e) {
            System.out.println("Error attaching subscription plans to model: " + e.getMessage());
            throw e;
        }
    }

}
