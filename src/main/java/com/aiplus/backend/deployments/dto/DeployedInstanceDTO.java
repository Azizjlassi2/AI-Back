package com.aiplus.backend.deployments.dto;

import java.time.LocalDateTime;

import com.aiplus.backend.deployments.model.DeployedInstance.InstanceStatus;
import com.aiplus.backend.subscription.dto.SubscriptionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeployedInstanceDTO {
    private Long id;
    private InstanceStatus status;
    private String instanceType;
    private String region;
    private int cpu;
    private int memory;
    private int storage;

    private float cpuUsage;
    private float memoryUsage;

    private LocalDateTime deployedAt;
    private String baseUrl;
    private int port;

    private int totalRequests;
    private int requestsPerMinute;
    private int successfulRequests;
    private int failedRequests;
    private float averageResponseTime;

    private SubscriptionDTO subscription;
}
    
