package com.aiplus.backend.datasets.mapper;

import org.mapstruct.Mapper;

import com.aiplus.backend.datasets.dto.TagDto;
import com.aiplus.backend.datasets.model.Tag;

/**
 * Mapper for converting Tag entities to and from TagDto.
 */
@Mapper(componentModel = "spring")
public interface TagMapper {

    /**
     * Converts a Tag entity to a TagDto.
     *
     * @param tag The source entity.
     * @return The mapped DTO.
     */
    TagDto toDto(Tag tag);

    /**
     * Converts a TagDto to a Tag entity.
     *
     * @param dto The source DTO.
     * @return The mapped entity (dataset reference ignored).
     */
    @org.mapstruct.Mapping(target = "dataset", ignore = true)
    Tag toEntity(TagDto dto);
}