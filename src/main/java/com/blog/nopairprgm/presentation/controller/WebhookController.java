package com.blog.nopairprgm.presentation.controller;

import com.blog.nopairprgm.application.service.PullRequestService;
import com.blog.nopairprgm.presentation.dto.WebhookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebhookController {
    private final PullRequestService pullRequestService;
    
    @PostMapping("/api/webhook/github")
    public ResponseEntity<Void> handlePullRequestWebhook(@RequestBody WebhookRequest request) {
        if ("opened".equals(request.getAction())) {
            pullRequestService.processPullRequest(request);
        }
        // 진입 테스트
        return ResponseEntity.ok().build();
    }
}
