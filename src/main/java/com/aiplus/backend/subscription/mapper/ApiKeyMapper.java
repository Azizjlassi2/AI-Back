package com.aiplus.backend.subscription.mapper;

import org.mapstruct.Mapper;

import com.aiplus.backend.subscription.dto.ApiKeyDTO;
import com.aiplus.backend.subscription.model.ApiKey;

@Mapper(componentModel = "spring", uses = { SubscriptionMapper.class })
public interface ApiKeyMapper {

    ApiKeyDTO toDTO(ApiKey apiKey);

    ApiKey toEntity(ApiKeyDTO dto);

}
