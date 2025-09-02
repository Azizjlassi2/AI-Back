package com.aiplus.backend.comments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.comments.dto.CreateModelCommentRequest;
import com.aiplus.backend.comments.dto.ModelCommentDto;
import com.aiplus.backend.comments.service.ModelCommentService;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/models/{modelId}/comments")
@RequiredArgsConstructor
public class ModelCommentController {

    private final ModelCommentService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ModelCommentDto>> create(@PathVariable Long modelId,
            @AuthenticationPrincipal User user, @Validated @RequestBody CreateModelCommentRequest req) {
        return ResponseEntity
                .ok(ResponseUtil.success("Comment created successfully", service.create(user, modelId, req)));
    }

}