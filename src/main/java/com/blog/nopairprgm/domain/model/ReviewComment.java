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
    private Integer line;  // position 대신 line 필드 사용
    @Column(columnDefinition = "TEXT")
    private String content;
    private String commitId;
    private LocalDateTime createdAt;
    private boolean published;
    private String patch;
    private String diffHunk;

    public static ReviewComment create(
            CodeReview review,
            String commitId,
            String path,
            Integer line,
            String content,
            String diffHunk
    ) {
        ReviewComment comment = new ReviewComment();
        comment.codeReview = review;
        comment.commitId = commitId;
        comment.path = path;
        comment.line = line;
        comment.content = content;
        comment.diffHunk = diffHunk;
        return comment;
    }

    public void markAsPublished() {
        this.published = true;
    }
}
