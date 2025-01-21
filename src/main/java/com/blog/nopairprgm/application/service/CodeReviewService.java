package com.blog.nopairprgm.application.service;

import com.blog.nopairprgm.domain.model.CodeReview;
import com.blog.nopairprgm.domain.model.PullRequest;
import com.blog.nopairprgm.domain.model.PullRequestChange;
import com.blog.nopairprgm.domain.repository.CodeReviewRepository;
import com.blog.nopairprgm.infrastructure.gemini.GeminiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CodeReviewService {
    private final CodeReviewRepository codeReviewRepository;
    private final GeminiClient geminiClient;

    @Transactional
    public CodeReview reviewCode(PullRequest pullRequest, PullRequestChange change) {
        String reviewContent = geminiClient.generateReview(
                formatCodeForReview(change)
        );

        CodeReview review = CodeReview.create(
                pullRequest,
                change,
                reviewContent
        );

        return codeReviewRepository.save(review);
    }

    private String formatCodeForReview(PullRequestChange change) {
        return """
            File: %s
            Changes:
            %s
            """.formatted(change.getFilename(), change.getPatch());
    }
}
