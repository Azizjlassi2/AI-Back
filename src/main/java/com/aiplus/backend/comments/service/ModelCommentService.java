package com.aiplus.backend.comments.service;

import org.springframework.stereotype.Service;

import com.aiplus.backend.comments.dto.CreateModelCommentRequest;
import com.aiplus.backend.comments.dto.ModelCommentDto;
import com.aiplus.backend.comments.factory.CommentFactory;
import com.aiplus.backend.comments.mapper.ModelCommentMapper;
import com.aiplus.backend.comments.model.ModelComment;
import com.aiplus.backend.comments.repository.ModelCommentRepository;
import com.aiplus.backend.models.exceptions.AiModelNotFoundException;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.models.repository.AiModelRepository;
import com.aiplus.backend.users.model.User;

import lombok.AllArgsConstructor;

/**
 * Service for managing comments for models and datasets.
 */
@AllArgsConstructor
@Service
public class ModelCommentService {

    private final ModelCommentRepository repository;
    private final AiModelRepository modelRepository;
    private final CommentFactory factory;
    private final ModelCommentMapper mapper;

    public ModelCommentDto create(User user, Long modelId, CreateModelCommentRequest req) {
        AiModel model = modelRepository.findById(modelId)
                .orElseThrow(() -> new AiModelNotFoundException("AI Model not found: " + modelId));
        ModelComment entity = factory.createModelComment(user, model, req.getContent());
        repository.save(entity);
        return mapper.toDto(entity);
    }

}
