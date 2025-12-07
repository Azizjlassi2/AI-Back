package com.aiplus.backend.deployments.service;

import com.aiplus.backend.deployments.factories.DeploymentFactory;
import com.aiplus.backend.deployments.model.DeployedInstance;
import com.aiplus.backend.deployments.repository.DeployedInstanceRepository;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.subscription.model.Subscription;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aiplus.backend.deployments.strategies.DeploymentStrategy;

@Service
@RequiredArgsConstructor
public class DeploymentService {

    private final DeploymentFactory deploymentFactory;
    private final DeployedInstanceRepository deployedInstanceRepository;

    @Value("${deployment.provider:RENDER}") // Configurable via properties
    private String defaultProvider;

    @Async
    public DeployedInstance deployModelForSubscription(Subscription subscription) {
        AiModel model = subscription.getPlan().getModel();
        DeploymentStrategy strategy = deploymentFactory.getStrategy(defaultProvider);
        DeployedInstance instance = strategy.deploy(model, subscription);
        return deployedInstanceRepository.save(instance);

    }
}