package com.aiplus.backend.deployments.proxy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ProxyFactory {
    private final Map<String, ProxyStrategy> strategies;

    public ProxyFactory(List<ProxyStrategy> strategyList) {
        this.strategies = strategyList.stream().collect(Collectors.toMap(s -> s.getClass().getSimpleName(), s -> s));
    }

    public ProxyStrategy getStrategy(String type) { // ex. "simpleProxy"
        return strategies.getOrDefault(type, strategies.get("simpleProxy")); // Default
    }
}