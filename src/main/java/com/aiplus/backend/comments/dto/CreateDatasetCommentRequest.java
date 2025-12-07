package com.aiplus.backend.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request object for creating a new comment on a dataset.
 */
@Data
public class CreateDatasetCommentRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 2048, message = "Content must be at most 2048 characters")
    private String content;
}