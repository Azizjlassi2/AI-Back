package com.aiplus.backend.subscriptionPlans.model;

import java.util.ArrayList;
import java.util.List;

import com.aiplus.backend.models.model.AiModel;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a subscription plan with details such as name, description, price,
 * billing period, features, and associated AI model. Attributes:
 * <ul>
 * <li>id: Unique identifier for the subscription plan.</li>
 * <li>name: Name of the subscription plan.</li>
 * <li>description: Description of the subscription plan.</li>
 * <li>price: Price of the subscription plan.</li>
 * <li>currency: Currency of the subscription plan price (default is TND).</li>
 * <li>billingPeriod: Billing period of the subscription plan (e.g., MONTHLY,
 * YEARLY).</li>
 * <li>features: List of features included in the subscription plan.</li>
 * <li>apiCallsLimit: API calls limit for the subscription plan.</li>
 * <li>model: The AI model associated with this subscription plan.</li>
 * </ul>
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the subscription plan.
     */
    private String name;

    /**
     * Description of the subscription plan.
     */
    @Column(columnDefinition = "TEXT", length = 2048)
    private String description;

    /**
     * Price of the subscription plan.
     */
    private double price;
    /**
     * Currency of the subscription plan price (default=TND).
     */
    private String currency = "TND";

    /**
     * Billing period of the subscription plan (e.g., MONTHLY, YEARLY).
     */
    @Enumerated(EnumType.STRING)
    private BillingPeriod billingPeriod;

    /**
     * Features included in the subscription plan.
     */
    @ElementCollection
    private List<String> features = new ArrayList<>();

    /**
     * API calls limit for the subscription plan.
     */
    private Integer apiCallsLimit;

    /**
     * The AI model associated with this subscription plan.
     */
    @ManyToOne
    @JoinColumn(name = "model_id")
    @JsonBackReference
    private AiModel model;

}
