package com.aiplus.backend.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aiplus.backend.comments.model.ModelComment;

public interface ModelCommentRepository extends JpaRepository<ModelComment, Long> {

}
