package com.aiplus.backend.comments.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.ReadOnlyProperty;
import com.aiplus.backend.users.dto.UserSummaryDto;
import lombok.Data;

/**
 * Data Transfer Object for user comments on AI models.
 */
@Data
public class ModelCommentDto {

    @ReadOnlyProperty
    private Long id;

    private UserSummaryDto user;

    private String content;

    private Long modelId;

    @ReadOnlyProperty
    private LocalDateTime date;
}