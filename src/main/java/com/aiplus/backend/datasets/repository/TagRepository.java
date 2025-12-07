package com.aiplus.backend.datasets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.datasets.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
