package com.aiplus.backend.datasets.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aiplus.backend.datasets.dto.DatasetCreateDTO;
import com.aiplus.backend.datasets.model.Dataset;
import com.aiplus.backend.datasets.service.DatasetService;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/developer/datasets")
@RequiredArgsConstructor
public class DatasetDeveloperController {
    private final ObjectMapper objectMapper;
    private final DatasetService datasetService;

    @PreAuthorize(value = "hasRole('DEVELOPER')")
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Dataset>>> getDeveloperDatasets(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ResponseUtil.success("Datasets found", datasetService.getDeveloperDatasets(user)));
    }

    // Create dataset endpoint
    @PreAuthorize("hasRole('DEVELOPER')")
    @PostMapping(value = "publish")
    public ResponseEntity<ApiResponse<Dataset>> createDataset(@Valid @RequestPart("metadata") String metadataJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal User user) {
        log.info("Files received: {}", (files != null ? files.size() : 0));
        log.info("Dataset metadata: {}", metadataJson);
        // Manually deserialize and validate the JSON string to DTO
        DatasetCreateDTO request;
        try {
            request = objectMapper.readValue(metadataJson, DatasetCreateDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid metadata format", e);

        }

        Dataset createdDataset = datasetService.createDataset(request, files, user);

        return ResponseEntity.ok(ResponseUtil.success("Dataset created successfully", createdDataset));
    }

}
