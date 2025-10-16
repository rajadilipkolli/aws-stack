package com.learning.awssns;

import static io.awspring.cloud.sns.core.SnsHeaders.NOTIFICATION_SUBJECT_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.fasterxml.jackson.databind.JsonNode;
import com.learning.awssns.common.AbstractIntegrationTest;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.awspring.cloud.sns.sms.SmsMessageAttributes;
import io.awspring.cloud.sns.sms.SmsType;
import io.awspring.cloud.sns.sms.SnsSmsTemplate;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

class SnsTopicIntegrationTest extends AbstractIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(SnsTopicIntegrationTest.class);

    private static final String QUEUE_NAME = "my-test-queue";

    @Autowired
    private SnsClient snsClient;

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Autowired
    private SnsTemplate snsTemplate;

    @Autowired
    private SnsSmsTemplate snsSmsTemplate;

    @Autowired
    LocalStackContainer localStackContainer;

    @Test
    void shouldSendAndReceiveSqsMessage() throws InterruptedException, ExecutionException {
        // Create a queue to receive messages
        String queueName = "test-topic-queue";
        String queueURL = this.createQueue(queueName)
                .thenApply(CreateQueueResponse::queueUrl)
                .get();
        String queueArn = this.sqsAsyncClient
                .getQueueAttributes(r -> r.queueUrl(queueURL).attributeNames(QueueAttributeName.QUEUE_ARN))
                .thenApply(t -> t.attributes().get(QueueAttributeName.QUEUE_ARN))
                .get();

        // Create a topic and subscribe the queue to it
        String topicName = "testTopic";
        String topicArn = this.createTopic(topicName);

        snsClient.subscribe(SubscribeRequest.builder()
                .protocol("sqs")
                .endpoint(queueArn)
                .topicArn(topicArn)
                .build());

        // Send a message to the topic
        snsTemplate.send(
                topicArn,
                MessageBuilder.withPayload("Spring Cloud AWS SNS Sample!")
                        .setHeader(NOTIFICATION_SUBJECT_HEADER, "Junit Header!")
                        .build());

        // Wait for and verify the message is received in the queue
        await().atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    ReceiveMessageResponse response = sqsAsyncClient
                            .receiveMessage(builder -> builder.queueUrl(queueURL))
                            .get();
                    List<Message> messages = response.messages();
                    assertThat(messages).isNotEmpty();
                    JsonNode body = objectMapper.readTree(messages.getFirst().body());
                    assertThat(body.get("Message").asText()).isEqualTo("Spring Cloud AWS SNS Sample!");
                    if (body.has("Subject")) {
                        assertThat(body.get("Subject").asText()).isEqualTo("Junit Header!");
                    }
                });
    }

    @Test
    void sendValidTextMessageUsesTopicChannelSendArnReadBySqs() throws InterruptedException, ExecutionException {
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        localStackContainer.followOutput(logConsumer);
        String queueURL = this.createQueue(QUEUE_NAME)
                .thenApply(CreateQueueResponse::queueUrl)
                .get();
        String queueArn = this.sqsAsyncClient
                .getQueueAttributes(r -> r.queueUrl(queueURL).attributeNames(QueueAttributeName.QUEUE_ARN))
                .thenApply(t -> t.attributes().get(QueueAttributeName.QUEUE_ARN))
                .get();
        String topicArn = this.createTopic("my-topic-name");

        snsClient.subscribe(r -> r.topicArn(topicArn).protocol("sqs").endpoint(queueArn));

        snsTemplate.convertAndSend(topicArn, "Spring Cloud AWS SNS Sample!");

        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            ReceiveMessageResponse response =
                    sqsAsyncClient.receiveMessage(r -> r.queueUrl(queueURL)).get();
            assertThat(response.hasMessages()).isTrue();
            JsonNode body = objectMapper.readTree(response.messages().getFirst().body());
            assertThat(body.get("Message").asText()).isEqualTo("Spring Cloud AWS SNS Sample!");
        });
    }

    @Test
    void sendValidMessage_ToPhoneNumber_WithAttributes() {
        assertDoesNotThrow(() -> snsSmsTemplate.send(
                "+919848022338",
                "Spring Cloud AWS got you covered!",
                SmsMessageAttributes.builder()
                        .smsType(SmsType.PROMOTIONAL)
                        .senderID("AWSPRING")
                        .maxPrice("1.00")
                        .build()));

        await().untilAsserted(() -> {
            String logs = localStackContainer.getLogs(OutputFrame.OutputType.STDOUT, OutputFrame.OutputType.STDERR);
            assertThat(logs).contains("Delivering SMS message to +919848022338: Spring Cloud AWS got you covered!");
        });
    }

    private CompletableFuture<CreateQueueResponse> createQueue(String queueName) {
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(queueName)
                .attributes(Map.of(QueueAttributeName.MESSAGE_RETENTION_PERIOD, "86400"))
                .build();

        return sqsAsyncClient.createQueue(createQueueRequest);
    }

    private String createTopic(String topicName) {
        CreateTopicRequest createQueueRequest = CreateTopicRequest.builder()
                .name(topicName)
                .attributes(Map.of("DisplayName", "LocalStack"))
                .build();

        return snsClient.createTopic(createQueueRequest).topicArn();
    }
}
