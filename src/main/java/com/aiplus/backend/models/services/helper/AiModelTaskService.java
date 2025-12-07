package com.aiplus.backend.models.services.helper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aiplus.backend.models.dto.TaskDto;
import com.aiplus.backend.models.exceptions.TaskNotFoundException;
import com.aiplus.backend.models.model.Task;
import com.aiplus.backend.models.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiModelTaskService {

    private final TaskRepository taskRepo;

    /**
     * Resolves tasks from DTO list. - If DTO has ID, loads existing task, updates
     * fields, and returns it. - If no ID, creates a new task with all fields from
     * DTO.
     *
     * @param taskDtos list of TaskDto from request
     * @return list of (updated or new) Task entities
     * @throws TaskNotFoundException if taskDto.id is present but not found in DB
     */
    public List<Task> resolveTasks(List<TaskDto> taskDtos) {
        if (taskDtos == null || taskDtos.isEmpty()) {
            return Collections.emptyList();
        }
        return taskDtos.stream().map(this::resolveSingleTask).toList();
    }

    private Task resolveSingleTask(TaskDto dto) {
        Task task;
        if (dto.getId() != null) {
            // Update existing
            task = taskRepo.findById(dto.getId()).orElseThrow(() -> new TaskNotFoundException(dto.getId()));
            // Update fields (manual; integrate taskMapper if available for partial updates)
            task.setName(dto.getName());
            // Add other fields as per Task entity (e.g.,
            // task.setDescription(dto.getDescription()); etc.)
        } else {
            // Create new with all fields
            task = new Task();
            task.setName(dto.getName());
            // Set other fields: task.setDescription(dto.getDescription()); etc.
        }
        return task; // No save here; defer to caller or model save
    }
}
