package com.learning.aws.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.util.BinaryUtils;
import tools.jackson.core.type.TypeReference;
import com.learning.aws.spring.common.AbstractIntegrationTest;
import com.learning.aws.spring.model.IpAddressDTO;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.integration.aws.support.AwsHeaders;
import org.springframework.messaging.Message;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.kinesis.retrieval.KinesisClientRecord;
import tools.jackson.core.JacksonException;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Test
    void contextLoads() throws InterruptedException, JacksonException {

        assertThat(this.messageBarrier.await(30, TimeUnit.SECONDS)).isTrue();

        Message<List<Record>> message = this.messageHolder.get();
        assertThat(message.getHeaders())
                .containsKeys(AwsHeaders.SHARD, AwsHeaders.RECEIVED_STREAM)
                .doesNotContainKeys(
                        AwsHeaders.RECEIVED_PARTITION_KEY,
                        AwsHeaders.RECEIVED_SEQUENCE_NUMBER,
                        AwsHeaders.STREAM,
                        AwsHeaders.PARTITION_KEY,
                        AwsHeaders.CHECKPOINTER);

        List<Record> payloadList = message.getPayload();

        assertThat(payloadList).isNotEmpty().hasSizeGreaterThan(1);

        Record item = payloadList.getFirst();
        assertThat(item).isNotNull();

        KinesisClientRecord kinesisClientRecord = KinesisClientRecord.fromRecord(item);

        String sequenceNumber = kinesisClientRecord.sequenceNumber();
        assertThat(sequenceNumber).isNotBlank();

        Instant approximateArrivalTimestamp = kinesisClientRecord.approximateArrivalTimestamp();
        assertThat(approximateArrivalTimestamp).isNotNull().isInstanceOf(Instant.class);

        String partitionKey = kinesisClientRecord.partitionKey();
        assertThat(partitionKey).isNotBlank();

        String dataAsString = new String(BinaryUtils.copyBytesFrom(kinesisClientRecord.data()));
        String payload = dataAsString.substring(dataAsString.indexOf("[{"));
        List<IpAddressDTO> ipAddressDTOS =
                objectMapper.readValue(payload, new TypeReference<>() {});
        assertThat(ipAddressDTOS).isNotEmpty().hasSize(254);
    }
}
