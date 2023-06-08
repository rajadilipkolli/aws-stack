package com.learning.aws.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learning.aws.spring.common.AbstractIntegrationTest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.integration.aws.support.AwsHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Test
    void contextLoads() throws InterruptedException, JsonProcessingException {

        assertThat(this.messageBarrier.await(30, TimeUnit.SECONDS)).isTrue();

        Message<List<?>> message = this.messageHolder.get();
        assertThat(message.getHeaders())
                .containsKeys(AwsHeaders.CHECKPOINTER, AwsHeaders.SHARD, AwsHeaders.RECEIVED_STREAM)
                .doesNotContainKeys(AwsHeaders.STREAM, AwsHeaders.PARTITION_KEY);

        List<?> payload = message.getPayload();
        assertThat(payload).hasSize(10);

        Object item = payload.get(0);

        assertThat(item).isInstanceOf(GenericMessage.class);

        Message<?> messageFromBatch = (Message<?>) item;

        assertThat(messageFromBatch.getPayload()).isEqualTo("Message0");
        assertThat(messageFromBatch.getHeaders()).containsEntry("event.eventType", "createEvent");
    }
}
