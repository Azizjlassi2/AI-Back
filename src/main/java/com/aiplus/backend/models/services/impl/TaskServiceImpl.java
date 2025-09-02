package com.aiplus.backend.models.services.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aiplus.backend.models.dto.TaskDto;
import com.aiplus.backend.models.exceptions.TaskNotFoundException;
import com.aiplus.backend.models.mapper.TaskMapper;
import com.aiplus.backend.models.model.Task;
import com.aiplus.backend.models.repository.TaskRepository;
import com.aiplus.backend.models.services.TaskService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository modelTaskRepository;
    private final TaskMapper taskMapper;

    @Override
    /*
     * Fetches a task by its ID.
     * Throws TaskNotFoundException if the task does not exist.
     */
    public TaskDto getTaskById(Long id) {
        return taskMapper.toDto(modelTaskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id)));
    }

    @Override
    /*
     * Fetches all tasks.
     * Returns an empty list if no tasks are found.
     */
    @Cacheable("tasks_all")
    public Page<TaskDto> getAllTasks(Pageable pageable) {
        return modelTaskRepository.findAll(pageable)
                .map(taskMapper::toDto);
    }

    @Override
    /*
     * Creates a new task.
     * Returns the created task as a ModelTaskDto.
     */
    @CacheEvict(value = "tasks_all", allEntries = true)
    public TaskDto createTask(TaskDto dto) {
        Task task = taskMapper.toEntity(dto);
        return taskMapper.toDto(modelTaskRepository.save(task));
    }

    @Override
    /*
     * Updates an existing task by ID.
     * Throws TaskNotFoundException if the task does not exist.
     * Returns the updated task as a ModelTaskDto.
     */
    @CacheEvict(value = "tasks_all", allEntries = true)
    public TaskDto updateTask(Long id, TaskDto dto) {
        Task existingTask = modelTaskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        existingTask.setName(dto.getName());
        return taskMapper.toDto(modelTaskRepository.save(existingTask));
    }

    @Override
    /*
     * Deletes a task by ID.
     * Throws TaskNotFoundException if the task does not exist.
     */
    @CacheEvict(value = "tasks_all", allEntries = true)
    public void deleteTask(Long id) {
        if (!modelTaskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        modelTaskRepository.deleteById(id);
    }

    @Override
    /*
     * Fetches tasks by name.
     * Returns a list of tasks whose names contain the specified string,
     * case-insensitive.
     */
    public List<TaskDto> getTasksByName(String name) {
        List<Task> tasks = modelTaskRepository.findByName(name);
        return tasks.stream()
                .map(taskMapper::toDto)
                .toList();
    }

}
