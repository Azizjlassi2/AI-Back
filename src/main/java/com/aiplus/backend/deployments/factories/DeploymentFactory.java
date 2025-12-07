package com.aiplus.backend.deployments.factories;

import com.aiplus.backend.deployments.strategies.DeploymentStrategy;
import com.aiplus.backend.deployments.strategies.RenderDeploymentStrategy;

import lombok.RequiredArgsConstructor;

import com.aiplus.backend.deployments.strategies.AwsDeploymentStrategy;

import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeploymentFactory {

    private final RenderDeploymentStrategy renderStrategy;
    private final AwsDeploymentStrategy awsStrategy;

    /**
     * Crée l'instance de strategy basée sur le provider.
     * 
     * @param provider Le nom du provider (e.g., "RENDER").
     * @return La strategy correspondante.
     */
    public DeploymentStrategy getStrategy(String provider) {
        return switch (provider.toUpperCase()) {
        case "RENDER" -> renderStrategy;
        case "AWS" -> awsStrategy;
        default -> throw new IllegalArgumentException("Provider non supporté: " + provider);
        };
    }
}