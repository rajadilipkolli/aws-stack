package com.learning.awspring;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.awspring.common.AbstractIntegrationTest;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SqsListenerIntegrationTest extends AbstractIntegrationTest {

    private static final String QUEUE_NAME = "test_queue";
    @Autowired private SqsAsyncClient sqsAsyncClient;

    @Autowired private SqsTemplate sqsTemplate;

    @BeforeAll
    void setUpQueue() throws ExecutionException, InterruptedException {
        this.createQueue().get();
    }

    @Test
    void shouldSendAndReceiveSqsMessageUsingSqsAsyncClient()
            throws ExecutionException, InterruptedException {
        String queueURL = this.getQueueURL().thenApply(GetQueueUrlResponse::queueUrl).get();

        this.sqsAsyncClient
                .sendMessage(request -> request.messageBody("test message").queueUrl(queueURL))
                .thenRun(() -> log.info("Message sent successfully to the Amazon sqs."));

        Awaitility.given()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(3))
                .await()
                .untilAsserted(
                        () -> {
                            List<Message> messages =
                                    sqsAsyncClient
                                            .receiveMessage(builder -> builder.queueUrl(queueURL))
                                            .thenApply(ReceiveMessageResponse::messages)
                                            .get();
                            assertThat(messages).isNotEmpty();
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

    CompletableFuture<GetQueueUrlResponse> getQueueURL() {
        return sqsAsyncClient.getQueueUrl(builder -> builder.queueName(QUEUE_NAME));
    }
}
