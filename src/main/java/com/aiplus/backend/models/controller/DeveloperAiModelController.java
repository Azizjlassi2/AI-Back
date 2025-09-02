package com.aiplus.backend.models.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.models.dto.AiModelCreateDto;
import com.aiplus.backend.models.dto.AiModelDto;
import com.aiplus.backend.models.services.AiModelService;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import lombok.AllArgsConstructor;

/**
 * Controller for managing Developer AI Models.
 * 
 */

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/developer/models")
public class DeveloperAiModelController {

    private final AiModelService modelService;

    // Endpoints for managing Developer AI Models

    /**
     * Creates a new AI model.
     *
     * @param dto The data transfer object containing the details of the AI model to
     *            create.
     * @return ResponseEntity containing the created AI model.
     */
    @PreAuthorize(value = "hasRole('DEVELOPER')")
    @PostMapping("/publish")
    public ResponseEntity<ApiResponse<AiModelDto>> create(@AuthenticationPrincipal User developer,
            @RequestBody AiModelCreateDto dto) {
        AiModelDto created = modelService.createModel(developer, dto);
        return ResponseEntity.ok(ResponseUtil.success("Model created", created));
    }

    /**
     * Updates an existing AI model.
     *
     * @param id  The ID of the AI model to update.
     * @param dto The data transfer object containing the updated details of the AI
     *            model.
     * @return ResponseEntity containing the updated AI model.
     */
    @PreAuthorize("hasRole('DEVELOPER') and @aiModelSecurity.isOwner(#id, authentication.name)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AiModelDto>> update(@PathVariable Long id, @RequestBody AiModelDto dto) {
        return ResponseEntity.ok(ResponseUtil.success("Model updated", modelService.updateModel(id, dto)));
    }

    /**
     * Deletes an AI model by its ID.
     *
     * @param id The ID of the AI model to delete.
     * @return ResponseEntity indicating the result of the delete operation.
     */
    @PreAuthorize("hasRole('DEVELOPER') and @aiModelSecurity.isOwner(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        modelService.deleteModel(id);
        return ResponseEntity.ok(ResponseUtil.success("Model deleted", null));
    }

    @PreAuthorize(value = "hasRole('DEVELOPER')")
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<AiModelDto>>> getDeveloperModels(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ResponseUtil.success("Models found", modelService.getDeveloperModels(user)));
    }

}
