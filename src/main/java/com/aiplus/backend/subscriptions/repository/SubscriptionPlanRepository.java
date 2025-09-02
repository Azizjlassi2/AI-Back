package com.aiplus.backend.subscriptions.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.subscriptions.model.SubscriptionPlan;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

}
