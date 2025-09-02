package com.aiplus.backend.subscriptions.model;

import java.util.ArrayList;
import java.util.List;

import com.aiplus.backend.models.model.AiModel;
import com.fasterxml.jackson.annotation.JsonBackReference;

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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private String currency;

    @Enumerated(EnumType.STRING)
    private BillingPeriod billingPeriod;

    @ElementCollection
    private List<String> features = new ArrayList<>();

    private Integer apiCallsLimit;
    private Double apiCallsPrice;

    @ManyToOne
    @JoinColumn(name = "model_id")
    @JsonBackReference
    private AiModel model;

}
