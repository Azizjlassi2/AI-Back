package com.aiplus.backend.datasets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.datasets.model.DatasetFile;

public interface DatasetFileRepository extends JpaRepository<DatasetFile, Long> {

}
