package com.aiplus.backend.subscription.model;

import java.time.LocalDate;

import com.aiplus.backend.payment.model.Payment;
import com.aiplus.backend.subscriptionPlans.model.SubscriptionPlan;
import com.aiplus.backend.users.model.DeveloperAccount;
import com.aiplus.backend.users.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a subscription of a client to a subscription plan. Attributes:
 * <ul>
 * <li>id: Unique identifier for the subscription.</li>
 * <li>client: The client account associated with this subscription.</li>
 * <li>plan: The subscription plan associated with this subscription.</li>
 * <li>developer: The developer account associated with this subscription.</li>
 * <li>startDate: The start date of the subscription.</li>
 * <li>nextBillingDate: The next billing date for the subscription.</li>
 * <li>status: The status of the subscription (e.g., PENDING, ACTIVE,
 * CANCELLED).</li>
 * <li>recurring: Indicates if the subscription is recurring or not.</li>
 * <li>webhookUrl: Optional custom webhook URL for the subscription.</li>
 * </ul>
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User client;

    @ManyToOne(optional = false)
    private SubscriptionPlan plan;

    /**
     * The developer who owns the AiModel for this subscription plan.
     */
    @ManyToOne(optional = false)
    private DeveloperAccount developer;

    /**
     * The payment associated with this subscription.
     */
    @ManyToOne(optional = true)
    private Payment payment;

    private LocalDate startDate;

    private LocalDate nextBillingDate;

    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private boolean recurring;

    // éventuellement stocker le webhook URL personnalisé si les abonnements en ont
    // un
    private String webhookUrl;
}