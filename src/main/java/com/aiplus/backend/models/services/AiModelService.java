package com.aiplus.backend.models.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aiplus.backend.models.dto.AiModelCreateDto;
import com.aiplus.backend.models.dto.AiModelDto;
import com.aiplus.backend.models.dto.AiModelSummaryDto;
import com.aiplus.backend.users.model.User;

public interface AiModelService {
    Page<AiModelSummaryDto> getAllModels(Pageable pageable);

    AiModelDto getModelById(User developer, Long id);

    List<AiModelSummaryDto> getModelsByDeveloperId(Long developerId);

    AiModelDto createModel(User developer, AiModelCreateDto dto);

    AiModelDto updateModel(Long id, AiModelDto dto);

    void deleteModel(Long id);

    List<AiModelSummaryDto> getModelsByName(String name);

    List<AiModelDto> getDeveloperModels(User user);

}
