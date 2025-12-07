package com.aiplus.backend.models.services.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aiplus.backend.models.exceptions.SubscriptionPlanNotFoundException;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.subscriptionPlans.dto.SubscriptionPlanDto;
import com.aiplus.backend.subscriptionPlans.mapper.SubscriptionPlanMapper;
import com.aiplus.backend.subscriptionPlans.model.SubscriptionPlan;
import com.aiplus.backend.subscriptionPlans.repository.SubscriptionPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiModelSubscriptionPlanService {

    private final SubscriptionPlanMapper spMapper; // Assuming this exists; use it for mapping if preferred
    private final SubscriptionPlanRepository spRepository;

    /**
     * Attaches/updates subscription plans from DTO list. - If DTO has ID, loads
     * existing plan, updates fields, and reattaches to model. - If no ID, creates a
     * new plan. - Assumes bidirectional relationship; caller must handle collection
     * management (e.g., no auto-delete).
     *
     * @param dtoList subscription plans from create/update DTO
     * @param model   target AiModel
     * @return list of (updated or new) SubscriptionPlan entities
     * @throws SubscriptionPlanNotFoundException if DTO ID is present but not found
     *                                           in DB
     */
    public List<SubscriptionPlan> attachSubscriptionPlans(List<SubscriptionPlanDto> dtoList, AiModel model) {
        List<SubscriptionPlan> plans = new ArrayList<>();
        if (dtoList != null && !dtoList.isEmpty()) {
            for (SubscriptionPlanDto dto : dtoList) {
                SubscriptionPlan plan;
                if (dto.getId() != null) {
                    // Update existing
                    plan = spRepository.findById(dto.getId())
                            .orElseThrow(() -> new SubscriptionPlanNotFoundException(dto.getId()));
                    // Update fields (use mapper if available, else manual)
                    // plan.setName(spMapper.updateFromDto(dto, plan).getName()); // Example with
                    // mapper
                    plan.setName(dto.getName());
                    plan.setDescription(dto.getDescription());
                    plan.setPrice(dto.getPrice());
                    plan.setFeatures(dto.getFeatures());
                    plan.setApiCallsLimit(dto.getApiCallsLimit());
                } else {
                    // Create new
                    plan = new SubscriptionPlan();
                    plan.setName(dto.getName());
                    plan.setDescription(dto.getDescription());
                    plan.setPrice(dto.getPrice());
                    plan.setFeatures(dto.getFeatures());
                    plan.setApiCallsLimit(dto.getApiCallsLimit());
                }
                plan.setModel(model);
                plans.add(spRepository.save(plan)); // Persist/merge
            }
        }
        return plans;
    }
}