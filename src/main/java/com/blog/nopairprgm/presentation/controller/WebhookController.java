package com.blog.nopairprgm.presentation.controller;

import com.blog.nopairprgm.application.service.PullRequestService;
import com.blog.nopairprgm.presentation.dto.WebhookRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public ResponseEntity<Void> handlePullRequestWebhook(@RequestBody String rawPayload) {
        log.info("Received webhook payload: {}", rawPayload);

        try {
            ObjectMapper mapper = new ObjectMapper();
            WebhookRequest request = mapper.readValue(rawPayload, WebhookRequest.class);

            if (Arrays.asList("opened", "reopened").contains(request.getAction())) {
                log.info("Processing PR: ID={}, Action={}",
                        request.getPullRequest().getId(),
                        request.getAction());
                pullRequestService.processPullRequest(request);
            } else {
                log.info("Skipping webhook: action={}", request.getAction());
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


}
