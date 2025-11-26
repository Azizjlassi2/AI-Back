package com.aiplus.backend.subscription.model;

import java.time.LocalDate;

import com.aiplus.backend.payment.model.Payment;
import com.aiplus.backend.subscriptionPlans.model.SubscriptionPlan;
import com.aiplus.backend.users.model.ClientAccount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a client subscription to a plan.
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

    /** Client owning the subscription. */
    @ManyToOne(optional = false)
    private ClientAccount client;

    /** Associated subscription plan. */
    @ManyToOne(optional = false)
    private SubscriptionPlan plan;

    /** Linked payment. */
    @OneToOne(optional = true, cascade = CascadeType.PERSIST)
    private Payment payment;

    /** Subscription start date. */
    private LocalDate startDate;

    /** Next billing date. */
    private LocalDate nextBillingDate;

    /** Subscription status (PENDING, ACTIVE, etc.). */
    @Column(nullable = false)
    private SubscriptionStatus status;

    /** True if recurring. */
    @Column(nullable = false)
    private boolean recurring;
}