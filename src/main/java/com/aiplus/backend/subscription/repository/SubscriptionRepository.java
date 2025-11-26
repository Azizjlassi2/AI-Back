package com.aiplus.backend.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.users.model.ClientAccount;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * Finds a subscription by the gateway transaction ID of its associated payment.
     * 
     * @param gatewayTransactionId the payment gateway transaction ID (payment_ref)
     * @return an Optional containing the subscription if found, else empty
     */
    Optional<Subscription> findByPaymentGatewayTransactionId(String gatewayTransactionId);

    /**
     * Find all subscriptions belonging to a given client.
     */
    List<Subscription> findByClient(ClientAccount client);

}