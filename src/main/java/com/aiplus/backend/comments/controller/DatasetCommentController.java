package com.aiplus.backend.comments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.comments.dto.CreateDatasetCommentRequest;
import com.aiplus.backend.comments.dto.DatasetCommentDto;
import com.aiplus.backend.comments.service.DatasetCommentService;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/datasets/{datasetId}/comments")
@RequiredArgsConstructor
public class DatasetCommentController {

    private final DatasetCommentService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DatasetCommentDto>> create(@PathVariable Long datasetId,
            @AuthenticationPrincipal User user, @Validated @RequestBody CreateDatasetCommentRequest req) {
        return ResponseEntity
                .ok(ResponseUtil.success("Comment created successfully", service.create(user, datasetId, req)));
    }
}
