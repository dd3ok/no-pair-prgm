package com.blog.nopairprgm.infrastructure.gemini;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "gemini")
@Component
@Getter
@Setter
public class GeminiProperties {
    private String apiKey;
    private String apiUrl;
    private Integer maxTokens;
}