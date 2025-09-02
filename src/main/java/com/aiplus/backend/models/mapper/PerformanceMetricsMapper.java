package com.aiplus.backend.models.mapper;

import org.mapstruct.Mapper;

import com.aiplus.backend.models.dto.PerformanceMetricsDto;
import com.aiplus.backend.models.model.PerformanceMetrics;

@Mapper(componentModel = "spring")
public interface PerformanceMetricsMapper {
    PerformanceMetricsDto toDto(PerformanceMetrics entity);

    PerformanceMetrics toEntity(PerformanceMetricsDto dto);
}
