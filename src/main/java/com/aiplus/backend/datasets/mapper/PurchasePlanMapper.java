package com.aiplus.backend.datasets.mapper;

import org.mapstruct.Mapper;

import com.aiplus.backend.datasets.dto.PurchasePlanDto;
import com.aiplus.backend.datasets.model.PurchasePlan;

/**
 * Mapper for converting PurchasePlan entities to and from PurchasePlanDto.
 */
@Mapper(componentModel = "spring")
public interface PurchasePlanMapper {

    /**
     * Converts a PurchasePlan entity to a PurchasePlanDto.
     *
     * @param purchasePlan The source entity.
     * @return The mapped DTO.
     */
    PurchasePlanDto toDto(PurchasePlan purchasePlan);

    /**
     * Converts a PurchasePlanDto to a PurchasePlan entity.
     *
     * @param dto The source DTO.
     * @return The mapped entity (dataset reference ignored).
     */
    @org.mapstruct.Mapping(target = "dataset", ignore = true)
    PurchasePlan toEntity(PurchasePlanDto dto);
}