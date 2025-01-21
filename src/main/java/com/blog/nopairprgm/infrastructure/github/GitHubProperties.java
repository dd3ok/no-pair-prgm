package com.blog.nopairprgm.infrastructure.github;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "github")
@Component
@Getter
@Setter
public class GitHubProperties {
    private String apiToken;
    private String apiUrl;
//    private String webhookSecret;
}

