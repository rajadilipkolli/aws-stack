package com.learning.aws.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learning.aws.spring.common.AbstractIntegrationTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.integration.aws.support.AwsHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    private static final String KINESIS_STREAM = "test_stream";

    @Test
    void contextLoads() throws InterruptedException, JsonProcessingException {
        PutRecordsRequest.Builder putRecordsRequest =
                PutRecordsRequest.builder().streamName(KINESIS_STREAM);

        List<PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Message<String> eventMessages =
                    MessageBuilder.withPayload("Message" + i)
                            .setHeader("event.eventType", "createEvent")
                            .build();
            PutRecordsRequestEntry putRecordsRequestEntry =
                    PutRecordsRequestEntry.builder()
                            .partitionKey("1")
                            .data(
                                    SdkBytes.fromByteArray(
                                            objectMapper.writeValueAsBytes(eventMessages)))
                            .build();
            putRecordsRequestEntryList.add(putRecordsRequestEntry);
        }
        putRecordsRequest.records(putRecordsRequestEntryList);

        amazonKinesis.putRecords(putRecordsRequest.build());

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
