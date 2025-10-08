package com.aiplus.backend.subscriptionPlans.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.subscriptionPlans.model.SubscriptionPlan;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

}
