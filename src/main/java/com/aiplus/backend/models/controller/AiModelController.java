package com.aiplus.backend.models.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.models.dto.AiModelDto;
import com.aiplus.backend.models.dto.AiModelSummaryDto;
import com.aiplus.backend.models.services.AiModelService;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import lombok.RequiredArgsConstructor;

/**
 * Controller for managing AI models. Provides endpoints to create, read,
 * update, and delete AI models.
 */
@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class AiModelController {
    private final AiModelService modelService;

    /**
     * Fetches all AI models.
     *
     * @return ResponseEntity containing a list of all AI models.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AiModelSummaryDto>>> getAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        return ResponseEntity.ok(ResponseUtil.success("Models fetched", modelService.getAllModels(pageable)));
    }

    /**
     * Fetches an AI model by its ID.
     *
     * @param id The ID of the AI model to fetch.
     * @return ResponseEntity containing the requested AI model.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AiModelDto>> getModelById(@AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ResponseUtil.success("Model found", modelService.getModelById(user, id)));
    }

    /**
     * Fetches all AI models associated with a specific developer.
     *
     * @param devId The ID of the developer whose models are to be fetched.
     * @return ResponseEntity containing a list of AI models for the specified
     *         developer.
     */
    @GetMapping("/developer/{devId}")
    public ResponseEntity<ApiResponse<List<AiModelSummaryDto>>> getModelsByDev(@PathVariable Long devId) {
        List<AiModelSummaryDto> models = modelService.getModelsByDeveloperId(devId);
        if (models.isEmpty()) {
            return ResponseEntity.ok(ResponseUtil.success("No models found for developer", models));
        }
        return ResponseEntity.ok(ResponseUtil.success("Models by developer", models));
    }

    /**
     * Searches for AI models by name.
     *
     * @param name The name of the AI model to search for.
     * @return ResponseEntity containing a list of AI models matching the search
     *         criteria.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AiModelSummaryDto>>> search(@RequestParam String name) {
        List<AiModelSummaryDto> models = modelService.getModelsByName(name);
        if (models.isEmpty()) {
            return ResponseEntity.ok(ResponseUtil.success("No models found", models));
        }
        return ResponseEntity.ok(ResponseUtil.success("Models found", models));
    }

}