package com.learning.awssns;

import static io.awspring.cloud.sns.core.SnsHeaders.NOTIFICATION_SUBJECT_HEADER;

import com.learning.awssns.common.AbstractIntegrationTest;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.awspring.cloud.sns.sms.SmsMessageAttributes;
import io.awspring.cloud.sns.sms.SmsType;
import io.awspring.cloud.sns.sms.SnsSmsTemplate;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

@Slf4j
class SnsTopicIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SnsClient snsClient;

    @Autowired
    private SnsTemplate snsTemplate;

    @Autowired
    private SnsSmsTemplate snsSmsTemplate;

    @Test
    void shouldSendAndReceiveSqsMessage() {
        String topicName = "testTopic";
        String topicArn = this.createTopic(topicName);

        snsClient.subscribe(SubscribeRequest.builder()
                .protocol("http")
                .endpoint("http://localhost:8080/testTopic")
                .topicArn(topicArn)
                .build());

        snsTemplate.send(
                topicArn,
                MessageBuilder.withPayload("Spring Cloud AWS SNS Sample!")
                        .setHeader(NOTIFICATION_SUBJECT_HEADER, "Junit Header!")
                        .build());

        snsSmsTemplate.send(
                "+919848022334",
                "Message to be delivered",
                SmsMessageAttributes.builder()
                        .smsType(SmsType.PROMOTIONAL)
                        .senderID("mySenderID")
                        .maxPrice("0.50")
                        .build());

        //        given()
        //                .atMost(Duration.ofSeconds(30))
        //                .pollInterval(Duration.ofSeconds(3))
        //                .await()
        //                .untilAsserted(() -> {
        //                    List<Message> messages = snsAsyncClient
        //                            .receiveMessage(builder -> builder.queueUrl(topicArn))
        //                            .thenApply(ReceiveTopicResponse::messages)
        //                            .get();
        //                    assertThat(messages).isNotEmpty();
        //                });
    }

    private String createTopic(String queueName) {
        CreateTopicRequest createQueueRequest = CreateTopicRequest.builder()
                .name(queueName)
                .attributes(Map.of("DisplayName", "LocalStack"))
                .build();

        return snsClient.createTopic(createQueueRequest).topicArn();
    }
}
