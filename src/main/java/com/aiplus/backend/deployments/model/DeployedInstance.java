package com.aiplus.backend.deployments.model;

import java.time.LocalDateTime;

import com.aiplus.backend.subscription.model.Subscription;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployedInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InstanceStatus status; // Enum: DEPLOYING, RUNNING, STOPPED, ERROR

    private String instanceType; // e.g., "free", "standard"
    private String region; // e.g., "oregon"
    private int cpu;
    private int memory;
    private int storage;

    private float cpuUsage;
    private float memoryUsage;

    private LocalDateTime deployedAt;
    private String baseUrl; // URL de l'instance déployée sur Render (e.g., https://model-xyz.onrender.com)
    private int port; // Port exposé (e.g., 8080)

    private int totalRequests;
    private int requestsPerMinute;
    private int successfulRequests;
    private int failedRequests;
    private float averageResponseTime;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription; // Lien vers la subscription/client

    // Enum pour status
    public enum InstanceStatus {
        DEPLOYING, RUNNING, STOPPED, ERROR
    }
}