package com.aiplus.backend.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.comments.model.DatasetComment;

public interface DatasetCommentRepository extends JpaRepository<DatasetComment, Long> {
}