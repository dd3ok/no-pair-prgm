package com.blog.nopairprgm.domain.repository;

import com.blog.nopairprgm.domain.model.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {
    boolean existsByGithubPrId(Long githubPrId);
    Optional<PullRequest> findByGithubPrId(Long githubPrId);
}