package com.roadcard.dockwebhook.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class AwsSqsDlqService {

    private static final Logger log = LoggerFactory.getLogger(AwsSqsDlqService.class);

    private final SqsClient sqsClient;
    private final String dlqUrl;

    public AwsSqsDlqService(@Value("${aws.region:us-east-1}") String region,
                            @Value("${aws.sqs.dlq-url:}") String dlqUrl) {
        this.sqsClient = SqsClient.builder().region(Region.of(region)).build();
        this.dlqUrl = dlqUrl;
    }

    public void sendToDlq(String message) {
        if (dlqUrl == null || dlqUrl.isBlank()) {
            log.warn("DLQ URL not configured; skipping DLQ send");
            return;
        }

        try {
            SendMessageRequest req = SendMessageRequest.builder()
                    .queueUrl(dlqUrl)
                    .messageBody(message)
                    .build();
            sqsClient.sendMessage(req);
            log.info("Sent message to DLQ {}", dlqUrl);
        } catch (SdkException e) {
            log.error("Failed to send message to DLQ: {}", e.getMessage(), e);
        }
    }
}
