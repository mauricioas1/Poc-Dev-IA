package com.roadcard.dockwebhook.controller;

import com.roadcard.dockwebhook.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks/dock")
@RequiredArgsConstructor
@Validated
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    private final WebhookService webhookService;

    @PostMapping
    public ResponseEntity<String> receive(@RequestBody String body) {
        log.info("Received Dock webhook request (raw size={} bytes)", body == null ? 0 : body.length());

        // Persist the raw envelope (non-blocking in service)
        webhookService.processAsync(body);

        // Return 200 immediately
        return ResponseEntity.ok("OK");
    }
}
