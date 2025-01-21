package com.blog.nopairprgm.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "pull_requests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PullRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer number;
    private Long githubPrId;
    private String repositoryName;
    private String title;
    private String author;
    private String baseBranch;
    private String headBranch;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "pullRequest")
    private List<PullRequestChange> changes = new ArrayList<>();

    public static PullRequest create(
            Long githubPrId,
            String repositoryName,
            String title,
            String author,
            String baseBranch,
            String headBranch,
            Integer number
    ) {
        PullRequest pr = new PullRequest();
        pr.githubPrId = githubPrId;
        pr.repositoryName = repositoryName;
        pr.title = title;
        pr.author = author;
        pr.baseBranch = baseBranch;
        pr.headBranch = headBranch;
        pr.createdAt = LocalDateTime.now();
        pr.number = number;
        return pr;
    }
}
