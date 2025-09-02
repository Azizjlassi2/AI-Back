package com.aiplus.backend.models.security;

import org.springframework.stereotype.Service;

import com.aiplus.backend.models.exceptions.AiModelNotFoundException;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.models.repository.AiModelRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AiModelSecurityService {
    private final AiModelRepository modelRepository;

    public boolean isOwner(Long modelId, String developerEmail) {
        AiModel model = modelRepository.findById(modelId)
                .orElseThrow(() -> new AiModelNotFoundException("Model not found"));
        return model.getDeveloperAccount().getUser().getEmail().equals(developerEmail);
    }

}
