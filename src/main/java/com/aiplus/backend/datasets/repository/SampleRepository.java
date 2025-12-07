package com.aiplus.backend.datasets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.datasets.model.Sample;

public interface SampleRepository extends JpaRepository<Sample, Long> {

}
