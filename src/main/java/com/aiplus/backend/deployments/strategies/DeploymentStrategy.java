package com.aiplus.backend.deployments.strategies;

import com.aiplus.backend.deployments.model.DeployedInstance;
import com.aiplus.backend.models.model.AiModel;
import com.aiplus.backend.subscription.model.Subscription;

/**
 * Interface Strategy pour les déploiements. Chaque provider implémente cette
 * interface.
 */
public interface DeploymentStrategy {

    /**
     * Déploie le modèle pour une subscription sur le provider spécifique.
     * 
     * @param model        Le modèle AI à déployer.
     * @param subscription La subscription activée.
     * @return L'instance déployée.
     * @throws RuntimeException Si le déploiement échoue.
     */
    DeployedInstance deploy(AiModel model, Subscription subscription);
}