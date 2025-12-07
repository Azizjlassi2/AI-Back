package com.aiplus.backend.deployments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.deployments.model.DeployedInstance;

public interface DeployedInstanceRepository extends JpaRepository<DeployedInstance, Long> {
}