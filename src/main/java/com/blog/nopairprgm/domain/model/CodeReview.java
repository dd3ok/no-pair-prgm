package com.blog.nopairprgm.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "code_reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CodeReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PullRequest pullRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    private PullRequestChange change;

    @Column(columnDefinition = "TEXT")
    private String reviewContent;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "codeReview")
    private List<ReviewComment> comments = new ArrayList<>();

    public static CodeReview create(
            PullRequest pullRequest,
            PullRequestChange change,
            String reviewContent
    ) {
        CodeReview review = new CodeReview();
        review.pullRequest = pullRequest;
        review.change = change;
        review.reviewContent = reviewContent;
        review.createdAt = LocalDateTime.now();
        return review;
    }
}
