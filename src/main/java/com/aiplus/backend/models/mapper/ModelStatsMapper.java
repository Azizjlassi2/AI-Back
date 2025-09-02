package com.aiplus.backend.models.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.aiplus.backend.models.dto.ModelStatsDto;
import com.aiplus.backend.models.model.ModelStats;

/**
 * Mapper interface for converting between ModelStats and ModelStatsDto.
 * This interface uses MapStruct to generate the implementation at compile time.
 */
@Mapper(componentModel = "spring")
public interface ModelStatsMapper {

    @Mapping(target = "used", source = "used")
    @Mapping(target = "stars", source = "stars")
    @Mapping(target = "discussions", source = "discussions")
    ModelStatsDto toDto(ModelStats modelStats);

    @Mapping(target = "used", source = "used")
    @Mapping(target = "stars", source = "stars")
    @Mapping(target = "discussions", source = "discussions")
    ModelStats toEntity(ModelStatsDto modelStatsDto);

}
