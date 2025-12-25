package com.aiplus.backend.deployments.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aiplus.backend.deployments.factories.DeploymentFactory;
import com.aiplus.backend.deployments.model.DeployedInstance;
import com.aiplus.backend.deployments.repository.DeployedInstanceRepository;
import com.aiplus.backend.deployments.strategies.DeploymentStrategy;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.subscription.model.Subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeploymentService {

    private final DeploymentFactory deploymentFactory;
    private final DeployedInstanceRepository deployedInstanceRepository;

    @Value("${deployment.provider:RENDER}")
    private String defaultProvider;

    @Async
    public DeployedInstance deployModelForSubscription(Subscription subscription) {
        log.info("Starting deployment for subscription ID: {}", subscription.getId());

        AiModel model = subscription.getPlan().getModel();
        log.info("Deploying model ID: {} for subscription ID: {}", model.getId(), subscription.getId());
        DeploymentStrategy strategy = deploymentFactory.getStrategy(defaultProvider);
        log.info("Using deployment strategy: {}", strategy.getClass().getSimpleName());
        DeployedInstance instance = strategy.deploy(model, subscription);
        log.info("Deployment completed for subscription ID: {}", subscription.getId());
        return deployedInstanceRepository.save(instance);

    }

}