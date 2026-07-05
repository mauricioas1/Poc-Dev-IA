package com.roadcard.dockwebhook.integration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class AwsSnsSqsIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(AwsSnsSqsIntegrationTest.class);

    @Test
    void snsPublishesToSqs() throws Exception {
        boolean dockerAvailable;
        try {
            dockerAvailable = DockerClientFactory.instance().isDockerAvailable();
        } catch (Exception ex) {
            dockerAvailable = false;
        }
        assumeTrue(dockerAvailable, "Docker is required for AwsSnsSqsIntegrationTest");

        try (LocalStackContainer localstack = new LocalStackContainer("localstack/localstack:1.4")
                .withServices(LocalStackContainer.Service.SNS, LocalStackContainer.Service.SQS)) {
            localstack.start();

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

            var attrs = sqs.getQueueAttributes(b -> b.queueUrl(queueUrl).attributeNamesWithStrings("QueueArn"));
            String queueArn = attrs.attributes().get("QueueArn");

            String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":\"SQS:SendMessage\",\"Resource\":\"" + queueArn + "\",\"Condition\":{\"ArnEquals\":{\"aws:SourceArn\":\"" + topicArn + "\"}}}]}";
            sqs.setQueueAttributes(b -> b.queueUrl(queueUrl).attributesWithStrings(java.util.Map.of("Policy", policy)));

            sns.subscribe(b -> b.topicArn(topicArn).protocol("sqs").endpoint(queueArn));
            sns.publish(b -> b.topicArn(topicArn).message("{\"hello\":\"world\"}"));

            ReceiveMessageRequest req = ReceiveMessageRequest.builder().queueUrl(queueUrl).waitTimeSeconds(5).maxNumberOfMessages(10).build();
            var resp = sqs.receiveMessage(req);
            assertFalse(resp.messages().isEmpty(), "Expected at least one message in SQS");
        }
    }
}
