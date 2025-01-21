package com.blog.nopairprgm.domain.repository;

import com.blog.nopairprgm.domain.model.CodeReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeReviewRepository extends JpaRepository<CodeReview, Long> {
    List<CodeReview> findByPullRequestId(Long pullRequestId);
}