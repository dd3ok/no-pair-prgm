package com.blog.nopairprgm.infrastructure.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestDetails {
    private Long id;
    private Integer number;
    private HeadDetails head;
    private BaseDetails base;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HeadDetails {
        private String sha;
        private String ref;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BaseDetails {
        private String sha;
        private String ref;
    }
}
