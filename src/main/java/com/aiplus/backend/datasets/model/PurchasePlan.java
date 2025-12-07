package com.aiplus.backend.datasets.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchasePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Double price = 0.0;

    @Builder.Default
    @Column(nullable = false, length = 3)
    private String currency = "TND";

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private BillingPeriod billingPeriod = BillingPeriod.ONE_TIME;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "purchase_plan_features", joinColumns = @JoinColumn(name = "purchase_plan_id"))
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;
}
