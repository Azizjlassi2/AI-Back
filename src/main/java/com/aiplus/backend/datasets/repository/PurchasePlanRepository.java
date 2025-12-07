package com.aiplus.backend.datasets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.datasets.model.PurchasePlan;

public interface PurchasePlanRepository extends JpaRepository<PurchasePlan, Long> {

}
