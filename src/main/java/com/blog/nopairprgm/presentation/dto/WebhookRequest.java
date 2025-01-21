package com.blog.nopairprgm.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
public class WebhookRequest {
    private final String action;
    private final PullRequestPayload pullRequest;
    private final RepositoryPayload repository;

    public WebhookRequest(
            @JsonProperty("action") String action,
            @JsonProperty("pull_request") PullRequestPayload pullRequest,
            @JsonProperty("repository") RepositoryPayload repository
    ) {
        this.action = action;
        this.pullRequest = pullRequest;
        this.repository = repository;
    }

    @Getter
    public static class PullRequestPayload {
        private final Long id;
        private final Integer number;
        private final String title;
        private final UserPayload user;
        private final BranchPayload base;
        private final BranchPayload head;

        public PullRequestPayload(
                @JsonProperty("id") Long id,
                @JsonProperty("number") Integer number,
                @JsonProperty("title") String title,
                @JsonProperty("user") UserPayload user,
                @JsonProperty("base") BranchPayload base,
                @JsonProperty("head") BranchPayload head
        ) {
            this.id = id;
            this.number = number;
            this.title = title;
            this.user = user;
            this.base = base;
            this.head = head;
        }
    }
    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UserPayload {
        private Long id;
        private String login;
        private String nodeId;
        private String avatarUrl;
    }

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RepositoryPayload {
        private Long id;
        private String name;
        private String fullName;
        private UserPayload owner;
    }

    @Getter
    public static class BranchPayload {
        private final String ref;

        public BranchPayload(
                @JsonProperty("ref") String ref
        ) {
            this.ref = ref;
        }
    }
}
