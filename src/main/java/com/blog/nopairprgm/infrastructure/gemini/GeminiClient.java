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
        String url = String.format("%s?key=%s",
                properties.getApiUrl(),
                properties.getApiKey());

        Map<String, Object> request = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", properties.getMaxTokens()
                )
        );

        try {
            log.info("Sending request to Gemini API");
            GeminiResponse response = restClient.post()
                    .uri(url)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            log.info("Successfully received response from Gemini");

            return Optional.ofNullable(response)
                    .map(GeminiResponse::getContent)
                    .orElseThrow(() -> new RuntimeException("Failed to get response from Gemini API"));
        } catch (RuntimeException e) {
            log.error("Failed to generate review - Error: {}", e.getMessage());
            log.error("Request body: {}", request);
            throw new RuntimeException("Failed to generate review", e);
        }
    }

    private String createReviewPrompt(String codeContent) {
        return """
            당신은 시니어 소프트웨어 엔지니어입니다. 다음 코드 변경사항을 리뷰하고 핵심적인 2-3가지 개선점을 제안해주세요.
            
            - 다음 내용을 중점적으로 봐주세요:
            1. 잠재적인 버그나 이슈
            2. 코드 스타일 개선점
            3. 성능 고려사항
            
            응답 형식:
            - 한국어로 작성해주세요
            - 이모지 사용이 가능합니다
            - 핵심적인 내용만 간단명료하게 설명해주세요
            - 실제 코드 예시가 있으면 더 좋습니다
            
            코드 변경사항:
            %s
            
            마크다운 형식으로 응답해주세요.
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
