package com.aiplus.backend.datasets.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiplus.backend.datasets.model.Dataset;
import com.aiplus.backend.datasets.model.Visibility;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findByDeveloperAccountId(Long developerId);

    Page<Dataset> findByVisibility(Visibility visibility, Pageable pageable);

}
