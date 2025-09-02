package com.aiplus.backend.endpoints.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.endpoints.model.Endpoint;

public interface EndpointRepository extends JpaRepository<Endpoint, Long> {

}
