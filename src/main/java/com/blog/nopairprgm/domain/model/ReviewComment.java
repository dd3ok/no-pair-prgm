package com.blog.nopairprgm.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "review_comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private CodeReview codeReview;

    private String path;
    private Integer position;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String commitId;
    private LocalDateTime createdAt;
    private boolean published;

    public static ReviewComment create(
            CodeReview codeReview,
            String path,
            Integer position,
            String content,
            String commitId
    ) {
        ReviewComment comment = new ReviewComment();
        comment.codeReview = codeReview;
        comment.path = path;
        comment.position = position;
        comment.content = content;
        comment.commitId = commitId;
        comment.createdAt = LocalDateTime.now();
        comment.published = false;
        return comment;
    }

    public void markAsPublished() {
        this.published = true;
    }
}
