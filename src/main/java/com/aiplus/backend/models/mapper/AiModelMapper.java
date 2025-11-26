package com.aiplus.backend.models.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.aiplus.backend.comments.mapper.ModelCommentMapper;
import com.aiplus.backend.endpoints.mapper.EndpointMapper;
import com.aiplus.backend.models.dto.AiModelCreateDto;
import com.aiplus.backend.models.dto.AiModelDto;
import com.aiplus.backend.models.dto.AiModelShortSummaryDto;
import com.aiplus.backend.models.dto.AiModelSummaryDto;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.subscriptionPlans.mapper.SubscriptionPlanMapper;
import com.aiplus.backend.users.mapper.UserSummaryMapper;

@Mapper(componentModel = "spring", uses = { TaskMapper.class, EndpointMapper.class, SubscriptionPlanMapper.class,
        ModelCommentMapper.class, UserSummaryMapper.class, })
public interface AiModelMapper {

    EndpointMapper endpointMapper = Mappers.getMapper(EndpointMapper.class);
    SubscriptionPlanMapper subscriptionPlanMapper = Mappers.getMapper(SubscriptionPlanMapper.class);
    TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
    ModelCommentMapper commentMapper = Mappers.getMapper(ModelCommentMapper.class);
    UserSummaryMapper userMapper = Mappers.getMapper(UserSummaryMapper.class);

    @Mapping(source = "image", target = "docker_image")
    @Mapping(target = "developerAccount", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AiModel toEntity(AiModelCreateDto dto);

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "developerAccount", ignore = true)
    AiModel toEntity(AiModelSummaryDto dto);

    @Mapping(source = "developerAccount.user", target = "developer")
    AiModelSummaryDto toSummaryDto(AiModel model);

    @Mapping(source = "developerAccount.user", target = "developer")
    AiModelDto toDto(AiModel model);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AiModel toEntity(AiModelDto dto);

    List<AiModelDto> toDtoList(List<AiModel> entities);

    List<AiModelSummaryDto> toSummaryDtoList(List<AiModel> entities);

    List<AiModel> toSummaryEntityList(List<AiModelSummaryDto> dtos);

    List<AiModel> toEntityList(List<AiModelDto> dtos);

    @Mapping(source = "id", target = "id")
    AiModelShortSummaryDto toShortSummaryDto(AiModel model);

}
