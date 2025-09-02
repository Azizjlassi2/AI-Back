package com.aiplus.backend.models.services.helper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.aiplus.backend.models.dto.AiModelDto;
import com.aiplus.backend.models.mapper.AiModelMapper;
import com.aiplus.backend.models.repository.AiModelRepository;
import com.aiplus.backend.users.model.User;

/**
 * Service class for managing AI models for developers.
 */
@AllArgsConstructor
@Service
public class AiModelDeveloperService {

    private final AiModelRepository modelRepository;
    private final AiModelMapper modelMapper;

    // get developer's AI models
    public List<AiModelDto> getDevelopersModels(User user) {
        return modelRepository.findByDeveloperAccountId(user.getAccount().getId()).stream().map(modelMapper::toDto)
                .collect(Collectors.toList());
    }
}
