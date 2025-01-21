package com.blog.nopairprgm.infrastructure.github;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PullRequestDetails {
    private Long id;
    private String nodeId;
    private String url;
    private String htmlUrl;
    private String diffUrl;
    private String patchUrl;
    private String issueUrl;
    private Integer number;
    private String state;
    private String title;
    private User user;
    private String body;
    private String createdAt;
    private String updatedAt;
    private String mergeCommitSha;
    private Head head;
    private Base base;

    @Getter
    public static class User {
        private String login;
        private Long id;
        private String nodeId;
        private String avatarUrl;
    }

    @Getter
    public static class Head {
        private String label;
        private String ref;
        private String sha;
        private Repository repo;
    }

    @Getter
    public static class Base {
        private String label;
        private String ref;
        private String sha;
        private Repository repo;
    }

    @Getter
    public static class Repository {
        private Long id;
        private String nodeId;
        private String name;
        private String fullName;
        private Boolean private_;
    }
}