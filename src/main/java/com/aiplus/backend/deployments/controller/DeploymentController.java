package com.aiplus.backend.deployments.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiplus.backend.deployments.dto.DeployedInstanceDTO;
import com.aiplus.backend.deployments.mapper.DeployedInstanceMapper;
import com.aiplus.backend.deployments.model.DeployedInstance;
import com.aiplus.backend.deployments.service.DeploymentService;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.subscription.repository.SubscriptionRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deployments")
public class DeploymentController {

    private final DeploymentService deploymentService;
    private final SubscriptionRepository subscriptionRepository;
    private final DeployedInstanceMapper deployedInstanceMapper;

    @PostMapping("/{id}")
    public DeployedInstanceDTO deployModel(@NonNull @PathVariable("id") Long subscriptionId) {

        log.info("Received deployment request for subscription ID: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with ID: " + subscriptionId));

        log.info("Deployment started for subscription ID: {}", subscriptionId);

        DeployedInstance instance = deploymentService.deployModelForSubscription(subscription);
        return deployedInstanceMapper.toDto(instance);
    }

}
