package com.aiplus.backend.models.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.models.model.Visibility;

public interface AiModelRepository extends JpaRepository<AiModel, Long> {

    List<AiModel> findByDeveloperAccountId(Long developerId);

    List<AiModel> findByNameContainingIgnoreCase(String name);

    List<AiModel> findByTasksNameContainingIgnoreCase(String taskName);

    List<AiModel> findByTasksNameIn(List<String> taskNames);

    /** Find all PUBLIC models, with paging */
    Page<AiModel> findByVisibility(Visibility visibility, Pageable pageable);

    boolean existsByName(String name);

}
