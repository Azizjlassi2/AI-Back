package com.aiplus.backend.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.aiplus.backend.comments.dto.ModelCommentDto;
import com.aiplus.backend.comments.model.ModelComment;
import com.aiplus.backend.users.mapper.UserSummaryMapper;

@Mapper(componentModel = "spring", uses = { UserSummaryMapper.class,

})
public interface ModelCommentMapper {
    UserSummaryMapper userMapper = Mappers.getMapper(UserSummaryMapper.class);

    @Mapping(source = "model.id", target = "modelId")
    ModelCommentDto toDto(ModelComment comment);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "date", ignore = true)
    ModelComment toEntity(ModelCommentDto dto);

}