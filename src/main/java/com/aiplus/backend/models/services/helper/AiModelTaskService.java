package com.aiplus.backend.models.services.helper;

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
     * Attaches tasks from DTO list. If ID present, load from DB; if missing, error.
     * 
     * @param taskDtos list of TaskDto from request
     * @return list of Task entities attached to model
     * @throws TaskNotFoundException if taskDto.id is null or not found
     */
    public List<Task> resolveTasks(List<TaskDto> taskDtos) {
        return taskDtos.stream().map(dto -> {

            // create new task
            return taskRepo.findById(dto.getId()).orElseGet(() -> {
                Task newTask = new Task();
                newTask.setName(dto.getName());
                return newTask;
            });
        }).toList();
    }

}
