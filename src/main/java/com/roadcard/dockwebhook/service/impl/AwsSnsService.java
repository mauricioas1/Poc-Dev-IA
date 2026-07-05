package com.roadcard.dockwebhook.service.impl;

import com.roadcard.dockwebhook.service.SnsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class AwsSnsService implements SnsService {

    private static final Logger log = LoggerFactory.getLogger(AwsSnsService.class);

    private final SnsClient snsClient;
    private final String topicArn;
    private final AwsSqsDlqService dlqService;

    public AwsSnsService(@Value("${aws.region:us-east-1}") String region,
                         @Value("${aws.sns.topic-arn:}") String topicArn,
                         AwsSqsDlqService dlqService) {
        this.snsClient = SnsClient.builder().region(Region.of(region)).build();
        this.topicArn = topicArn;
        this.dlqService = dlqService;
    }

    @Override
    public void publish(String message, String eventType) throws Exception {
        if (topicArn == null || topicArn.isBlank()) {
            log.warn("SNS topic ARN not configured; skipping publish");
            return;
        }

        try {
            PublishRequest.Builder builder = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message);

            if (eventType != null && !eventType.isBlank()) {
                builder.messageAttributesEntry("EventType",
                        software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue(eventType)
                                .build());
            }

            PublishRequest req = builder.build();

            snsClient.publish(req);
            log.info("Published message to SNS topic {} with eventType={}", topicArn, eventType);
        } catch (SdkException e) {
            log.error("Failed to publish to SNS: {}. Sending to DLQ.", e.getMessage(), e);
            try {
                dlqService.sendToDlq(message);
            } catch (Exception ex) {
                log.error("Failed to send to DLQ: {}", ex.getMessage(), ex);
            }
            throw e;
        }
    }
}
