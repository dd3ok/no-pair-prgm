package com.blog.nopairprgm.infrastructure.github;

import com.blog.nopairprgm.domain.model.ReviewComment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubClient {
    private final GitHubProperties properties;
    private final RestClient restClient;

    public List<PullRequestFiles> getPullRequestFiles(String repository, Long prNumber) {
        String url = String.format("%s/repos/%s/pulls/%d/files",
                properties.getApiUrl(), repository, prNumber);

        return restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + properties.getApiToken())
                .retrieve()
                .body(new ParameterizedTypeReference<List<PullRequestFiles>>() {});
    }

    public void createReviewComment(
            String repository,
            Long prNumber,
            String commitId,
            String path,
            Integer position,
            String body
    ) {
        String url = String.format("%s/repos/%s/pulls/%d/comments",
                properties.getApiUrl(), repository, prNumber);

        Map<String, Object> request = Map.of(
                "commit_id", commitId,
                "path", path,
                "position", position,
                "body", body
        );

        restClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + properties.getApiToken())
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public String getLatestCommitId(String repository, Long prNumber) {
        PullRequestDetails details = getPullRequestDetails(repository, prNumber);
        return details.getHead().getSha();
    }

    public PullRequestDetails getPullRequestDetails(String repository, Long prNumber) {
        String url = String.format("%s/repos/%s/pulls/%d",
                properties.getApiUrl(), repository, prNumber);

        return restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + properties.getApiToken())
                .retrieve()
                .body(PullRequestDetails.class);
    }

    public void createPullRequestReview(
            String repository,
            Long prNumber,
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

        restClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + properties.getApiToken())
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
