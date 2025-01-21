package com.blog.nopairprgm.application.service;

import com.blog.nopairprgm.domain.model.CodeReview;
import com.blog.nopairprgm.domain.model.PullRequest;
import com.blog.nopairprgm.domain.model.PullRequestChange;
import com.blog.nopairprgm.domain.repository.PullRequestChangeRepository;
import com.blog.nopairprgm.domain.repository.PullRequestRepository;
import com.blog.nopairprgm.infrastructure.github.GitHubClient;
import com.blog.nopairprgm.infrastructure.github.PullRequestFiles;
import com.blog.nopairprgm.presentation.dto.WebhookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PullRequestService {
    private final PullRequestRepository pullRequestRepository;
    private final PullRequestChangeRepository changeRepository;
    private final GitHubClient githubClient;
    private final CodeReviewService codeReviewService;
    private final ReviewCommentService reviewCommentService;

    @Transactional
    public void processPullRequest(WebhookRequest webhook) {
        WebhookRequest.PullRequestPayload pr = webhook.getPullRequest();

        log.info("Processing PR - ID: {}, Number: {}", pr.getId(), pr.getNumber());

        if (pullRequestRepository.existsByGithubPrNumber(pr.getNumber())) {
            log.info("Pull request number {} already exists", pr.getNumber());
            return;
        }

        PullRequest pullRequest = PullRequest.create(
                pr.getId(),
                webhook.getRepository().getFullName(),
                pr.getTitle(),
                pr.getUser().getLogin(),
                pr.getBase().getRef(),
                pr.getHead().getRef(),
                pr.getNumber()
        );

        pullRequestRepository.save(pullRequest);
        processCodeReview(pullRequest);
    }


    @Transactional
    public void analyzeCode(Long pullRequestId) {
        PullRequest pullRequest = pullRequestRepository.findById(pullRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Pull request not found: " + pullRequestId));

        log.info("Analyzing PR - ID: {}, Number: {}, Repository: {}",
                pullRequestId,
                pullRequest.getGithubPrNumber(),
                pullRequest.getRepositoryName()
        );

        List<PullRequestFiles> files = githubClient.getPullRequestFiles(
                pullRequest.getRepositoryName(),
                pullRequest.getGithubPrNumber()
        );

        files.forEach(file -> {
            PullRequestChange change = PullRequestChange.create(
                    pullRequest,
                    file.getFilename(),
                    file.getPatch(),
                    file.getAdditions(),
                    file.getDeletions()
            );
            changeRepository.save(change);
        });
    }

    @Transactional
    public void processCodeReview(PullRequest pr) {
        try {
            // 1. 코드 변경사항 분석
            analyzeCode(pr.getId());

            // 2. 각 변경사항에 대한 리뷰 생성
            List<PullRequestChange> changes = changeRepository.findByPullRequestId(pr.getId());
            String commitId = githubClient.getLatestCommitId(
                    pr.getRepositoryName(),
                    pr.getGithubPrNumber()  // githubPrId -> githubPrNumber로 변경
            );

            changes.forEach(change -> {
                if (shouldReview(change)) {
                    CodeReview review = codeReviewService.reviewCode(pr, change);
                    createReviewComment(pr, change, review, commitId);
                }
            });

            // 3. 리뷰 코멘트 발행
            reviewCommentService.publishPendingComments();

        } catch (Exception e) {
            log.error("Error processing code review for PR {}: {}",
                    pr.getGithubPrNumber(), e.getMessage(), e);  // githubPrId -> githubPrNumber로 변경
            throw new RuntimeException("Failed to process code review", e);
        }
    }


    private boolean shouldReview(PullRequestChange change) {
        // 리뷰가 필요한 파일인지 확인
        String filename = change.getFilename().toLowerCase();

        // 테스트 파일이나 리소스 파일 제외
        if (filename.contains("test/") || filename.contains("/test/")) {
            return false;
        }

        // 특정 확장자만 리뷰
        return filename.endsWith(".java") ||
                filename.endsWith(".kt") ||
                filename.endsWith(".scala");
    }

    private void createReviewComment(
            PullRequest pullRequest,
            PullRequestChange change,
            CodeReview review,
            String commitId
    ) {
        reviewCommentService.createComment(
                review,
                change.getFilename(),
                calculatePosition(change.getPatch()),
                review.getReviewContent(),
                commitId
        );
    }

    private Integer calculatePosition(String patch) {
        if (patch == null || patch.isEmpty()) {
            return 1;
        }

        // patch 내용에서 변경된 라인 위치 계산
        String[] lines = patch.split("\n");
        int position = 1;

        for (String line : lines) {
            if (line.startsWith("+") || line.startsWith("-")) {
                position++;
            }
        }

        return position;
    }
}
