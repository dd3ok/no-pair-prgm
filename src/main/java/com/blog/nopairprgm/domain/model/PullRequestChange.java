package com.blog.nopairprgm.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "pull_request_changes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PullRequestChange {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PullRequest pullRequest;

    private String filename;
    @Column(columnDefinition = "TEXT")
    private String patch;
    private Integer additions;
    private Integer deletions;

    @OneToMany(mappedBy = "change")
    private List<CodeReview> reviews = new ArrayList<>();
    
    public static PullRequestChange create(
            PullRequest pullRequest,
            String filename,
            String patch,
            Integer additions,
            Integer deletions
    ) {
        PullRequestChange change = new PullRequestChange();
        change.pullRequest = pullRequest;
        change.filename = filename;
        change.patch = patch;
        change.additions = additions;
        change.deletions = deletions;
        return change;
    }
}
