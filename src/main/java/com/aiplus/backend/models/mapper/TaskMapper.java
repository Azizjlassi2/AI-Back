package com.aiplus.backend.models.mapper;

import org.mapstruct.Mapper;

import com.aiplus.backend.models.dto.TaskDto;
import com.aiplus.backend.models.model.Task;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto toDto(Task task);

    Task toEntity(TaskDto taskDto);

    List<TaskDto> toDtoList(List<Task> tasks);

    List<Task> toEntityList(List<TaskDto> taskDtos);

}
