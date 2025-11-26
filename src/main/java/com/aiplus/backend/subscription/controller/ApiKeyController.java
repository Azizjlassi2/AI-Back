package com.aiplus.backend.subscription.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.subscription.dto.ApiKeyDTO;
import com.aiplus.backend.subscription.mapper.ApiKeyMapper;
import com.aiplus.backend.subscription.service.ApiKeyService;
import com.aiplus.backend.users.model.User;
import com.aiplus.backend.utils.responses.ApiResponse;
import com.aiplus.backend.utils.responses.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final ApiKeyMapper apiKeyMapper;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ApiKeyDTO>>> getApiKey(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ResponseUtil.success("Api Keys Retreived ",
                apiKeyService.getApiKeysByClientId(user.getId()).stream().map(apiKeyMapper::toDTO).toList()));
    }

}
