package com.blog.nopairprgm.infrastructure.github;

import com.blog.nopairprgm.domain.model.ReviewComment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubClient {
    private final GitHubProperties properties;
    private final RestClient restClient;

    public List<PullRequestFiles> getPullRequestFiles(String repository, Integer prNumber) {
        String url = String.format("%s/repos/%s/pulls/%d/files",
                properties.getApiUrl(), repository, prNumber);

        log.info("=== GitHub API Request Files ===");
        log.info("Files URL: {}", url);
        log.info("Repository: {}, PR Number: {}", repository, prNumber);
        log.info("Token (first 10 chars): {}",
                properties.getApiToken().substring(0, Math.min(10, properties.getApiToken().length())));

        try {
            var response = restClient.get()
                    .uri(url)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "Bearer " + properties.getApiToken())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<PullRequestFiles>>() {
                    });

            log.info("Files Response Status: {}", response.getStatusCode());
            log.info("Files Response Headers: {}", response.getHeaders());
            log.info("Files Count: {}", response.getBody().size());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Failed to get PR files. URL: {}, Status: {}, Response: {}",
                    url, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }


    // 1. 먼저 리뷰를 생성
    public Integer createReview(String repository, Integer prNumber, String commitId, String body) {
        String url = String.format("%s/repos/%s/pulls/%d/reviews",
                properties.getApiUrl(), repository, prNumber);

        Map<String, Object> request = Map.of(
                "commit_id", commitId,
                "body", body,      // 리뷰 본문 추가
                "event", "COMMENT",
                "comments", List.of()  // 빈 코멘트 리스트로 시작
        );

        try {
            var response = restClient.post()
                    .uri(url)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "Bearer " + properties.getApiToken())
                    .body(request)
                    .retrieve()
                    .toEntity(Map.class);

            return (Integer) response.getBody().get("id");
        } catch (Exception e) {
            log.error("Failed to create review: {}", e.getMessage());
            throw new RuntimeException("Failed to create review", e);
        }
    }

    // 2. 리뷰에 코멘트 추가
    public void createReviewComment(
            String repository,
            Integer prNumber,
            Integer reviewId,
            String commitId,
            String path,
            String body,
            Integer line
    ) {
        String url = String.format("%s/repos/%s/pulls/%d/reviews/%d/comments",
                properties.getApiUrl(), repository, prNumber, reviewId);

        Map<String, Object> request = Map.of(
                "body", body,
                "commit_id", commitId,
                "path", path,
                "line", line // position 대신 line 사용
        );

        try {
            restClient.post()
                    .uri(url)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "Bearer " + properties.getApiToken())
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully created review comment");
        } catch (Exception e) {
            log.error("Failed to create review comment: {}", e.getMessage());
            throw new RuntimeException("Failed to create review comment", e);
        }
    }

    public String getLatestCommitId(String repository, Integer prNumber) {
        log.info("Getting latest commit for repository: {} PR: {}", repository, prNumber);

        try {
            PullRequestDetails details = getPullRequestDetails(repository, prNumber);
            return details.getHead().getSha();
        } catch (Exception e) {
            log.error("Failed to get latest commit ID: {}", e.getMessage());
            throw new RuntimeException("Failed to get latest commit ID", e);
        }
    }

    public PullRequestDetails getPullRequestDetails(String repository, Integer prNumber) throws JsonProcessingException {
        String url = String.format("%s/repos/%s/pulls/%d",
                properties.getApiUrl(), repository, prNumber);

        log.info("=== GitHub API Request Details ===");
        log.info("URL: {}", url);
        log.info("Token (first 10 chars): {}",
                properties.getApiToken().substring(0, Math.min(10, properties.getApiToken().length())));

        try {
            var response = restClient.get()
                    .uri(url)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "Bearer " + properties.getApiToken())
                    .retrieve()
                    .toEntity(PullRequestDetails.class);

            log.info("Response Status: {}", response.getStatusCode());
            log.info("Response Headers: {}", response.getHeaders());
            log.info("Response Body: {}", response.getBody());
            log.info("Response Body Details: {}",
                    new ObjectMapper().writeValueAsString(response.getBody()));

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("GitHub API Error Details:");
            log.error("Status code: {}", e.getStatusCode());
            log.error("Response body: {}", e.getResponseBodyAsString());
            log.error("Raw headers: {}", e.getResponseHeaders());
            throw e;
        }
    }


    public void createPullRequestReview(
            String repository,
            Integer prNumber,
            String commitId,
            List<ReviewComment> comments
    ) {
        String url = String.format("%s/repos/%s/pulls/%d/reviews",
                properties.getApiUrl(), repository, prNumber);

        Map<String, Object> request = Map.of(
                "commit_id", commitId,
                "event", "COMMENT",
                "comments", comments
        );

        try {
            restClient.post()
                    .uri(url)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "Bearer " + properties.getApiToken())
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully created pull request review");
        } catch (Exception e) {
            log.error("Failed to create pull request review: {}", e.getMessage());
            throw new RuntimeException("Failed to create pull request review", e);
        }
    }
}
