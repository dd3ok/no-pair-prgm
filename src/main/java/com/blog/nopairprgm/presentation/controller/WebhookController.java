package com.blog.nopairprgm.presentation.controller;

import com.blog.nopairprgm.application.service.PullRequestService;
import com.blog.nopairprgm.presentation.dto.WebhookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebhookController {
    private final PullRequestService pullRequestService;

    @PostMapping("/api/webhook/github")
    public ResponseEntity<Void> handlePullRequestWebhook(@RequestBody WebhookRequest request) {
        log.info("=== Webhook Received ===");
        log.info("Action: {}", request.getAction());
        log.info("PR ID: {}", request.getPullRequest().getId());
        log.info("PR Number: {}", request.getPullRequest().getNumber());
        log.info("Repository: {}", request.getRepository().getFullName());
        log.info("=======================");

        if (Arrays.asList("opened", "reopened", "synchronize").contains(request.getAction())) {
            pullRequestService.processPullRequest(request);
        }
        return ResponseEntity.ok().build();
    }
}
