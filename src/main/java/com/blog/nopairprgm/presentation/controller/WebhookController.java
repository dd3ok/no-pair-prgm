package com.blog.nopairprgm.presentation.controller;

import com.blog.nopairprgm.application.service.PullRequestService;
import com.blog.nopairprgm.presentation.dto.WebhookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebhookController {
    private final PullRequestService pullRequestService;

    @PostMapping("/api/webhook/github")
    public ResponseEntity<Void> handlePullRequestWebhook(@RequestBody WebhookRequest request) {
        log.info("Received webhook - PR ID: {}, Number: {}, Action: {}",
                request.getPullRequest().getId(),
                request.getPullRequest().getNumber(),  // number 필드 추가 필요
                request.getAction()
        );
        log.info("Repository: {}", request.getRepository().getFullName());

        if ("opened".equals(request.getAction())) {
            pullRequestService.processPullRequest(request);
        }
        return ResponseEntity.ok().build();
    }

}
