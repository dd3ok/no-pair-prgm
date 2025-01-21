package com.blog.nopairprgm.domain.repository;

import com.blog.nopairprgm.domain.model.PullRequestChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PullRequestChangeRepository extends JpaRepository<PullRequestChange, Long> {
    List<PullRequestChange> findByPullRequestId(Long pullRequestId);
}
