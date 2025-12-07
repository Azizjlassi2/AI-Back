package com.aiplus.backend.deployments.strategies;

import org.springframework.stereotype.Component;

import com.aiplus.backend.deployments.model.DeployedInstance;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.subscription.model.Subscription;
// Import AWS SDK (ajoutez dépendance Maven: software.amazon.awssdk:ecs)

@Component
public class AwsDeploymentStrategy implements DeploymentStrategy {

    // Injectez AWS clients (e.g., EcsClient)

    @Override
    public DeployedInstance deploy(AiModel model, Subscription subscription) {
        // Logique AWS : Créez task definition ECS avec image Docker
        // Exécutez task, obtenez endpoint (e.g., via Load Balancer)
        String baseUrl = "https://aws-instance-url"; // Placeholder
        return DeployedInstance.builder().subscription(subscription).status(DeployedInstance.InstanceStatus.DEPLOYING)
                .baseUrl(baseUrl)
                // ...
                .build();
    }
}