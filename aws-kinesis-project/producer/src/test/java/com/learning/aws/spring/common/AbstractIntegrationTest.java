package com.learning.aws.spring.common;

import static com.learning.aws.spring.utils.AppConstants.PROFILE_TEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.aws.spring.TestKinesisProducerApplication;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

@ActiveProfiles({PROFILE_TEST})
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = {
            "spring.cloud.stream.kinesis.bindings.eventConsumerBatchProcessingWithHeaders-in-0.consumer.idleBetweenPolls = 1",
            "spring.cloud.stream.kinesis.bindings.eventConsumerBatchProcessingWithHeaders-in-0.consumer.listenerMode = batch",
            "spring.cloud.stream.kinesis.bindings.eventConsumerBatchProcessingWithHeaders-in-0.consumer.checkpointMode = manual",
            "spring.cloud.stream.bindings.eventConsumerBatchProcessingWithHeaders-in-0.consumer.useNativeDecoding = true",
            "spring.cloud.stream.kinesis.binder.headers = event.eventType",
            "spring.cloud.stream.kinesis.binder.autoAddShards = true"
        },
        classes = ContainerConfig.class)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @Autowired protected MockMvc mockMvc;

    @Autowired protected ObjectMapper objectMapper;

    @Autowired protected KinesisAsyncClient amazonKinesis;

    @Autowired protected CountDownLatch messageBarrier;

    @Autowired protected AtomicReference<Message<List<?>>> messageHolder;
}
