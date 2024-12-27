package com.learning.aws.spring;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.KINESIS;

import com.learning.aws.spring.common.ContainerConfig;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

@TestConfiguration(proxyBeanMethods = false)
public class TestKinesisProducerApplication {

    @Bean
    KinesisAsyncClient amazonKinesis(LocalStackContainer localStackContainer) {
        return KinesisAsyncClient.builder()
                .endpointOverride(localStackContainer.getEndpointOverride(KINESIS))
                .region(Region.of(localStackContainer.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        localStackContainer.getAccessKey(),
                                        localStackContainer.getSecretKey())))
                .build();
    }

    @Bean
    public AtomicReference<Message<List<Message<?>>>> messageHolder() {
        return new AtomicReference<>();
    }

    @Bean
    public CountDownLatch messageBarrier() {
        return new CountDownLatch(1);
    }

    @Bean
    public Consumer<Message<List<Message<?>>>> eventConsumerBatchProcessingWithHeaders() {
        return eventMessages -> {
            messageHolder().set(eventMessages);
            messageBarrier().countDown();
        };
    }

    public static void main(String[] args) {
        SpringApplication.from(KinesisProducerApplication::main)
                .with(ContainerConfig.class)
                .run(args);
    }
}
