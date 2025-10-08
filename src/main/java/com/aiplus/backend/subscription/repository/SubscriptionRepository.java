package com.aiplus.backend.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiplus.backend.subscription.model.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    // plus méthodes custom si besoin

}