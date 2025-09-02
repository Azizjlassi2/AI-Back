package com.aiplus.backend.endpoints.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.aiplus.backend.endpoints.dto.EndpointDto;
import com.aiplus.backend.endpoints.model.Endpoint;

@Mapper(componentModel = "spring")
public interface EndpointMapper {

    EndpointDto toDto(Endpoint entity);

    @Mapping(target = "model", ignore = true)
    @Mapping(target = "id", ignore = true)
    Endpoint toEntity(EndpointDto dto);

    List<EndpointDto> toDtoList(List<Endpoint> entities);

    List<Endpoint> toEntityList(List<EndpointDto> dtos);
}