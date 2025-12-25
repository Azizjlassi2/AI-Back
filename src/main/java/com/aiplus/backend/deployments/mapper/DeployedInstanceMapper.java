package com.aiplus.backend.deployments.mapper;

import org.mapstruct.Mapper;

import com.aiplus.backend.deployments.dto.DeployedInstanceDTO;
import com.aiplus.backend.deployments.model.DeployedInstance;
import com.aiplus.backend.subscription.mapper.SubscriptionMapper;

@Mapper(componentModel = "spring", uses = { SubscriptionMapper.class })
public interface DeployedInstanceMapper {
    DeployedInstanceDTO toDto(DeployedInstance entity);

    DeployedInstance toEntity(DeployedInstanceDTO dto);
}
