package com.aiplus.backend.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiplus.backend.subscription.model.ApiKey;
import com.aiplus.backend.subscription.model.Subscription;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<Subscription> findBySubscriptionId(Long subscriptionId);

    List<ApiKey> findAllBySubscriptionClientId(Long id);

}
