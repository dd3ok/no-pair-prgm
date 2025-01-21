package com.blog.nopairprgm.domain.repository;

import com.blog.nopairprgm.domain.model.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    List<ReviewComment> findByPublishedFalse();
    List<ReviewComment> findByCodeReviewId(Long codeReviewId);
}