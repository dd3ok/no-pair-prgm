package com.blog.nopairprgm.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookRequest {
    private String action;
    private PullRequestPayload pullRequest;
    private RepositoryPayload repository;

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PullRequestPayload {
        private Long id;
        private Integer number;
        private String title;
        private UserPayload user;
        private String body;
        private BranchPayload base;
        private BranchPayload head;
    }

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BranchPayload {
        private String ref;
        private String sha;
        private RepositoryPayload repo;
    }

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserPayload {
        private Long id;
        private String login;
        private String nodeId;
        private String avatarUrl;
    }

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RepositoryPayload {
        private Long id;
        private String name;
        private String fullName;
        private UserPayload owner;
    }
}
