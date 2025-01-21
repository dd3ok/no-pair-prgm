package com.blog.nopairprgm.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    public static class RepositoryPayload {
        private final String fullName;

        public RepositoryPayload(
                @JsonProperty("full_name") String fullName
        ) {
            this.fullName = fullName;
        }
    }

    @Getter
    public static class UserPayload {
        private final String login;

        public UserPayload(
                @JsonProperty("login") String login
        ) {
            this.login = login;
        }
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
