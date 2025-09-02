package com.aiplus.backend.models.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.models.dto.TaskDto;
import com.aiplus.backend.models.services.TaskService;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller for managing model tasks. Provides endpoints to create, read,
 * update, and delete tasks associated with AI models.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Fetches all tasks.
     *
     * @return ResponseEntity containing a list of all tasks.
     */
    /**
     * GET /api/v1/tasks Optional request params: page = zero-based page index
     * (default 0) size = page size (default 20) sort = property to sort by (default
     * "id") dir = sort direction ("asc" or "desc", default "asc")
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TaskDto>>> getAllTasks(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<TaskDto> tasksPage = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(ResponseUtil.success("Tasks fetched", tasksPage));
    }

    /**
     * Fetches a task by its ID.
     *
     * @param id The ID of the task to fetch.
     * @return ResponseEntity containing the requested task.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDto>> getTaskById(@PathVariable Long id) {
        TaskDto dto = taskService.getTaskById(id);
        return ResponseEntity.ok(ResponseUtil.success("Task found", dto));
    }

    /**
     * Searches for tasks by name.
     *
     * @param name The name or part of the name to search for.
     * @return ResponseEntity containing a list of tasks that match the search
     *         criteria.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TaskDto>>> searchByName(@RequestParam String name) {
        List<TaskDto> list = taskService.getTasksByName(name);
        return ResponseEntity.ok(ResponseUtil.success("Tasks fetched by name", list));
    }

    /**
     * Creates a new task.
     *
     * @param dto The task data to create.
     * @return ResponseEntity containing the created task.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TaskDto>> createTask(@Valid @RequestBody TaskDto dto) {
        TaskDto created = taskService.createTask(dto);
        return ResponseEntity.status(201).body(ResponseUtil.success("Task created", created));
    }

    /**
     * Updates an existing task.
     *
     * @param id  The ID of the task to update.
     * @param dto The updated task data.
     * 
     * @return ResponseEntity containing the updated task.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDto>> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDto dto) {
        TaskDto updated = taskService.updateTask(id, dto);
        return ResponseEntity.ok(ResponseUtil.success("Task updated", updated));
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id The ID of the task to delete.
     * @return ResponseEntity indicating the result of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ResponseUtil.success("Task deleted", null));
    }
}