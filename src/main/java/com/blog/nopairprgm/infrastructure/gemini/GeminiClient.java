package com.blog.nopairprgm.infrastructure.gemini;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiClient {
    private final GeminiProperties properties;
    private final RestClient restClient;

    public String generateReview(String codeContent) {
        String prompt = createReviewPrompt(codeContent);
        return callGeminiApi(prompt);
    }

    public String analyzeCode(String code) {
        String prompt = createAnalysisPrompt(code);
        return callGeminiApi(prompt);
    }

    public String suggestImprovements(String code) {
        String prompt = createImprovementPrompt(code);
        return callGeminiApi(prompt);
    }

    public String checkSecurityIssues(String code) {
        String prompt = createSecurityCheckPrompt(code);
        return callGeminiApi(prompt);
    }

    private String callGeminiApi(String prompt) {
        String url = properties.getApiUrl() + "/generateContent";

        Map<String, Object> request = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", properties.getMaxTokens()
                )
        );

        GeminiResponse response = restClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + properties.getApiKey())
                .body(request)
                .retrieve()
                .body(GeminiResponse.class);

        return Optional.ofNullable(response)
                .map(GeminiResponse::getContent)
                .orElseThrow(() -> new RuntimeException("Failed to get response from Gemini API"));
    }

    private String createReviewPrompt(String codeContent) {
        return """
                As a senior software engineer, review the following code changes and provide:
                1. Potential bugs or issues
                2. Code style improvements
                3. Performance considerations
                4. Security concerns
                
                Code changes:
                %s
                
                Please format the response in markdown.
                """.formatted(codeContent);
    }

    private String createAnalysisPrompt(String code) {
        return """
                Analyze the following code and provide insights about:
                1. Code complexity
                2. Design patterns used
                3. Potential technical debt
                
                Code:
                %s
                """.formatted(code);
    }

    private String createImprovementPrompt(String code) {
        // 개선사항 관련 프롬프트
        return """
                Suggest improvements for the following code focusing on:
                1. Clean code principles
                2. Best practices
                3. Readability
                
                Code:
                %s
                """.formatted(code);
    }

    private String createSecurityCheckPrompt(String code) {
        // 보안 검사 관련 프롬프트
        return """
                Check for security issues in the following code:
                1. Common vulnerabilities
                2. Input validation
                3. Authentication/Authorization concerns
                
                Code:
                %s
                """.formatted(code);
    }

    @Getter
    public static class GeminiResponse {
        private final String content;

        @JsonCreator
        public GeminiResponse(@JsonProperty("candidates") List<Map<String, Object>> candidates) {
            this.content = Optional.ofNullable(candidates)
                    .filter(c -> !c.isEmpty())
                    .map(c -> c.get(0))
                    .map(c -> ((Map<String, Object>) c.get("content")))
                    .map(c -> ((List<Map<String, Object>>) c.get("parts")))
                    .filter(parts -> !parts.isEmpty())
                    .map(parts -> parts.get(0))
                    .map(part -> (String) part.get("text"))
                    .orElse("");
        }
    }
}
