package com.aiplus.backend.comments.service;

import org.springframework.stereotype.Service;

import com.aiplus.backend.comments.dto.CreateDatasetCommentRequest;
import com.aiplus.backend.comments.dto.DatasetCommentDto;
import com.aiplus.backend.comments.factory.CommentFactory;
import com.aiplus.backend.comments.mapper.DatasetCommentMapper;
import com.aiplus.backend.comments.model.DatasetComment;
import com.aiplus.backend.comments.repository.DatasetCommentRepository;
import com.aiplus.backend.datasets.exception.DatasetNotFoundException;
import com.aiplus.backend.datasets.model.Dataset;
import com.aiplus.backend.datasets.repository.DatasetRepository;
import com.aiplus.backend.users.model.User;

import lombok.AllArgsConstructor;

/**
 * Service for managing comments for datasets.
 */
@AllArgsConstructor
@Service
public class DatasetCommentService {

    private final DatasetCommentRepository repository;
    private final DatasetRepository datasetRepository;
    private final CommentFactory factory;
    private final DatasetCommentMapper mapper;

    public DatasetCommentDto create(User user, Long datasetId, CreateDatasetCommentRequest req) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new DatasetNotFoundException("Dataset not found: " + datasetId));
        DatasetComment entity = factory.createDatasetComment(user, dataset, req.getContent());
        repository.save(entity);
        return mapper.toDto(entity);
    }
}