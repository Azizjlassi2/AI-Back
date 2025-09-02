package com.aiplus.backend.subscriptions.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.aiplus.backend.subscriptions.dto.SubscriptionPlanDto;
import com.aiplus.backend.subscriptions.model.SubscriptionPlan;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanMapper {
    SubscriptionPlanDto toDto(SubscriptionPlan entity);

    @Mapping(target = "model", ignore = true)
    SubscriptionPlan toSubscriptionPlanEntity(SubscriptionPlanDto dto);

    List<SubscriptionPlanDto> toDtoList(List<SubscriptionPlan> entities);

}