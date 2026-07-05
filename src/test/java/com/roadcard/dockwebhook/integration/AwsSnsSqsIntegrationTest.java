package com.roadcard.dockwebhook.integration;

import com.roadcard.dockwebhook.service.impl.AwsSnsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
class AwsSnsSqsIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(AwsSnsSqsIntegrationTest.class);

    @Container
    public static LocalStackContainer localstack = new LocalStackContainer("localstack/localstack:1.4")
            .withServices(LocalStackContainer.Service.SNS, LocalStackContainer.Service.SQS);

    @Test
    void snsPublishesToSqs() throws Exception {
        String endpoint = localstack.getEndpointOverride(LocalStackContainer.Service.SNS).toString();

        SnsClient sns = SnsClient.builder()
                .endpointOverride(new URI(localstack.getEndpointOverride(LocalStackContainer.Service.SNS).toString()))
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .build();

        SqsClient sqs = SqsClient.builder()
                .endpointOverride(new URI(localstack.getEndpointOverride(LocalStackContainer.Service.SQS).toString()))
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .build();

        String topicArn = sns.createTopic(CreateTopicRequest.builder().name("test-topic").build()).topicArn();
        String queueUrl = sqs.createQueue(CreateQueueRequest.builder().queueName("test-queue").build()).queueUrl();

        // Resolve queue ARN
        var attrs = sqs.getQueueAttributes(b -> b.queueUrl(queueUrl).attributeNamesWithStrings("QueueArn"));
        String queueArn = attrs.attributes().get("QueueArn");

        // Allow SNS to send messages to the queue (policy)
        String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":\"SQS:SendMessage\",\"Resource\":\"" + queueArn + "\",\"Condition\":{\"ArnEquals\":{\"aws:SourceArn\":\"" + topicArn + "\"}}}]}";
        sqs.setQueueAttributes(b -> b.queueUrl(queueUrl).attributesWithStrings(java.util.Map.of("Policy", policy)));

        // Subscribe SQS queue to SNS topic
        sns.subscribe(b -> b.topicArn(topicArn).protocol("sqs").endpoint(queueArn));

        // Publish message
        sns.publish(b -> b.topicArn(topicArn).message("{\"hello\":\"world\"}"));

        ReceiveMessageRequest req = ReceiveMessageRequest.builder().queueUrl(queueUrl).waitTimeSeconds(5).maxNumberOfMessages(10).build();
        var resp = sqs.receiveMessage(req);
        assertFalse(resp.messages().isEmpty(), "Expected at least one message in SQS");
    }
}
