package com.blog.nopairprgm.infrastructure.github;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PullRequestFiles {
    private String sha;
    private String filename;
    private String status;
    private Integer additions;
    private Integer deletions;
    private Integer changes;
    private String patch;
    private String blobUrl;
    private String rawUrl;
    private String contentsUrl;
}