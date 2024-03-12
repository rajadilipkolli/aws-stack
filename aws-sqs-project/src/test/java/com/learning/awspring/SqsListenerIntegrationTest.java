package com.learning.awspring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.given;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.model.SQSMessagePayload;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Slf4j
class SqsListenerIntegrationTest extends AbstractIntegrationTest {

    private static final String QUEUE_NAME = "test_queue";
    @Autowired private SqsAsyncClient sqsAsyncClient;

    @Autowired private SqsTemplate sqsTemplate;

    @Test
    void shouldSendAndReceiveSqsMessageUsingSqsAsyncClient()
            throws ExecutionException, InterruptedException {
        String queueURL = this.createQueue().thenApply(CreateQueueResponse::queueUrl).get();

        this.sqsAsyncClient
                .sendMessage(request -> request.messageBody("test message").queueUrl(queueURL))
                .thenRun(() -> log.info("Message sent successfully to the Amazon sqs."));

        given().atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(3))
                .await()
                .untilAsserted(
                        () -> {
                            List<Message> messages =
                                    sqsAsyncClient
                                            .receiveMessage(builder -> builder.queueUrl(queueURL))
                                            .thenApply(ReceiveMessageResponse::messages)
                                            .get();
                            assertThat(messages).isNotEmpty().hasSize(1);
                        });
    }

    @Test
    void shouldSendAndReceiveSqsMessageUsingSqsTemplate() {
        this.createQueue()
                .thenRun(
                        () ->
                                this.sqsTemplate.sendAsync(
                                        to ->
                                                to.queue(QUEUE_NAME)
                                                        .payload(
                                                                new SQSMessagePayload(
                                                                        "1", "test message"))));

        given().atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(3))
                .await()
                .untilAsserted(
                        () -> {
                            Collection<org.springframework.messaging.Message<SQSMessagePayload>>
                                    messages =
                                            sqsTemplate.receiveMany(
                                                    from ->
                                                            from.queue(QUEUE_NAME)
                                                                    .visibilityTimeout(
                                                                            Duration.ofSeconds(10)),
                                                    SQSMessagePayload.class);
                            assertThat(messages).isNotEmpty().hasSize(1);
                        });
    }

    private CompletableFuture<CreateQueueResponse> createQueue() {
        CreateQueueRequest createQueueRequest =
                CreateQueueRequest.builder()
                        .queueName(SqsListenerIntegrationTest.QUEUE_NAME)
                        .attributes(Map.of(QueueAttributeName.MESSAGE_RETENTION_PERIOD, "86400"))
                        .build();

        return sqsAsyncClient.createQueue(createQueueRequest);
    }
}
