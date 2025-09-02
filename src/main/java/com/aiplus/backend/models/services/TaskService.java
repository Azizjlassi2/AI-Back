package com.aiplus.backend.models.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aiplus.backend.models.dto.TaskDto;

public interface TaskService {

    TaskDto getTaskById(Long id);

    Page<TaskDto> getAllTasks(Pageable pageable);

    TaskDto createTask(TaskDto dto);

    TaskDto updateTask(Long id, TaskDto dto);

    void deleteTask(Long id);

    List<TaskDto> getTasksByName(String name);
}
