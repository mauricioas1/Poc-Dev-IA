package com.roadcard.dockwebhook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);


    private final DecryptService decryptService;
    private final FilePersistenceService persistenceService;
    private final SnsService snsService;
    private final com.roadcard.dockwebhook.service.IdempotencyService idempotencyService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final jakarta.validation.Validator validator;

    @Async
    public void processAsync(String rawEnvelope) {
        try {
            persistenceService.saveRawEnvelope(rawEnvelope);
        } catch (Exception e) {
            log.warn("Failed to persist raw envelope: {}", e.getMessage());
        }

        try {
            String decrypted = decryptService.decrypt(rawEnvelope);
            log.info("Decryption succeeded; parsing payload");

            JsonNode root = mapper.readTree(decrypted);

            // Determine idempotency key
            String key = null;
            if (root.hasNonNull("transaction_uuid")) {
                key = root.get("transaction_uuid").asText();
            } else if (root.hasNonNull("purchase_id")) {
                key = "purchase:" + root.get("purchase_id").asText();
            }

            boolean newlyRegistered = idempotencyService.register(key == null ? "" : key);
            if (!newlyRegistered) {
                log.info("Duplicate event detected (key={}); skipping processing", key);
                return;
            }

            // Map to DTO and validate
            try {
                com.roadcard.dockwebhook.dto.PurchaseEventDto dto = mapper.treeToValue(root, com.roadcard.dockwebhook.dto.PurchaseEventDto.class);
                var violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    log.warn("Validation failed for decrypted event: {} violations", violations.size());
                }
            } catch (Exception e) {
                log.warn("Failed to map decrypted JSON to PurchaseEventDto: {}", e.getMessage());
            }

            // Identify event type (based on provided fields).
            String eventType = null;
            if (root.hasNonNull("event")) {
                eventType = root.get("event").asText();
            } else if (root.hasNonNull("status_id")) {
                int sid = root.get("status_id").asInt(-1);
                if (sid == 0) eventType = "Purchase_processed";
                else if (sid == 3) eventType = "Purchase_approved";
            } else if (root.hasNonNull("status")) {
                String status = root.get("status").asText();
                if ("Processada".equalsIgnoreCase(status)) eventType = "Purchase_processed";
                else if ("Pendente".equalsIgnoreCase(status)) eventType = "Purchase_approved";
            }

            log.info("Event identified: {}", eventType == null ? "unknown" : eventType);

            // Map to specific DTOs and apply basic business validation per event
            if ("Purchase_approved".equalsIgnoreCase(eventType)) {
                try {
                    com.roadcard.dockwebhook.dto.PurchaseApprovedDto approved = mapper.treeToValue(root, com.roadcard.dockwebhook.dto.PurchaseApprovedDto.class);
                    var violations = validator.validate(approved);
                    if (!violations.isEmpty()) {
                        log.warn("Purchase_approved validation failed: {} violations", violations.size());
                    }
                } catch (Exception e) {
                    log.warn("Failed to map to PurchaseApprovedDto: {}", e.getMessage());
                }
            } else if ("Purchase_processed".equalsIgnoreCase(eventType)) {
                try {
                    com.roadcard.dockwebhook.dto.PurchaseProcessedDto proc = mapper.treeToValue(root, com.roadcard.dockwebhook.dto.PurchaseProcessedDto.class);
                    var violations = validator.validate(proc);
                    if (!violations.isEmpty()) {
                        log.warn("Purchase_processed validation failed: {} violations", violations.size());
                    }
                } catch (Exception e) {
                    log.warn("Failed to map to PurchaseProcessedDto: {}", e.getMessage());
                }
            }

            log.info("Publishing raw decrypted JSON to SNS (with EventType attribute)");
            snsService.publish(decrypted, eventType);
        } catch (Exception ex) {
            log.error("Error processing webhook: {}", ex.getMessage(), ex);
        }
    }
}
