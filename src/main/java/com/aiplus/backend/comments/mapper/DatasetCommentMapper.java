package com.aiplus.backend.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.aiplus.backend.comments.dto.DatasetCommentDto;
import com.aiplus.backend.comments.model.DatasetComment;
import com.aiplus.backend.users.mapper.UserSummaryMapper;

@Mapper(componentModel = "spring", uses = { UserSummaryMapper.class })
public interface DatasetCommentMapper {
    UserSummaryMapper userMapper = Mappers.getMapper(UserSummaryMapper.class);

    @Mapping(source = "dataset.id", target = "datasetId")
    DatasetCommentDto toDto(DatasetComment comment);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "dataset", ignore = true)
    @Mapping(target = "date", ignore = true)
    DatasetComment toEntity(DatasetCommentDto dto);
}