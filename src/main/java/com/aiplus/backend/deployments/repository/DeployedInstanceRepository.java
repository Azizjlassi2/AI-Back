package com.aiplus.backend.deployments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.aiplus.backend.deployments.model.DeployedInstance;

public interface DeployedInstanceRepository extends JpaRepository<DeployedInstance, Long> {

    @Query("SELECT i FROM DeployedInstance i WHERE i.subscription.plan.model.name = :modelName AND i.subscription.client.id = :userId AND i.status = 'RUNNING'")
    Optional<DeployedInstance> findActiveByModelNameAndUser(String modelName, Long userId);
}
