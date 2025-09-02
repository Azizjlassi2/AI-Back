package com.aiplus.backend.favorites.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.favorites.service.FavoriteService;
import com.aiplus.backend.models.dto.AiModelSummaryDto;
import com.aiplus.backend.models.mapper.AiModelMapper;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final AiModelMapper aiModelMapper;

    /**
     * Gets all favorite models for the authenticated user.
     */
    @GetMapping("/models")
    public ResponseEntity<ApiResponse<List<AiModelSummaryDto>>> getAllFavorites(@AuthenticationPrincipal User user) {
        List<AiModelSummaryDto> favoriteModelDtos = aiModelMapper
                .toSummaryDtoList(favoriteService.getFavoriteModelsForUser(user.getId()));
        return ResponseEntity.ok(ResponseUtil.success("Models found", favoriteModelDtos));
    }

    /**
     * Adds a model to the user's favorites.
     */
    @PostMapping("/models/{id}")
    public ResponseEntity<ApiResponse<List<AiModelSummaryDto>>> addFavoriteModel(@AuthenticationPrincipal User user,
            @PathVariable Long id) {
        List<AiModelSummaryDto> savedModelDtos = aiModelMapper
                .toSummaryDtoList(favoriteService.addModelToFavorites(id, user));
        return ResponseEntity.ok(ResponseUtil.success("Model added to favorites", savedModelDtos));
    }

    /**
     * Removes a model from the user's favorites.
     */
    @DeleteMapping("/models/{id}")
    public ResponseEntity<ApiResponse<List<AiModelSummaryDto>>> removeFavoriteModel(@AuthenticationPrincipal User user,
            @PathVariable Long id) {
        List<AiModelSummaryDto> updatedModelDtos = aiModelMapper
                .toSummaryDtoList(favoriteService.removeModelFromFavorites(id, user));
        return ResponseEntity.ok(ResponseUtil.success("Model removed from favorites", updatedModelDtos));
    }

}
