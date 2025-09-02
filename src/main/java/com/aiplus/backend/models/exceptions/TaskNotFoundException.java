package com.aiplus.backend.models.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task with ID " + id + " not found");
    }

    public TaskNotFoundException(String name) {
        super("Task with name '" + name + "' not found");
    }

}
