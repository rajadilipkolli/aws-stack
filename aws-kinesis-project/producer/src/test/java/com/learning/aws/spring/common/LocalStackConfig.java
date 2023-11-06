package com.learning.aws.spring.common;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class LocalStackConfig {

    static final LocalStackContainer localStackContainer =
            new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack").withTag("2.3.2"));

    static {
        localStackContainer.start();
        System.setProperty(
                "spring.cloud.aws.endpoint", localStackContainer.getEndpoint().toString());
        System.setProperty(
                "spring.cloud.aws.credentials.access-key", localStackContainer.getAccessKey());
        System.setProperty(
                "spring.cloud.aws.credentials.secret-key", localStackContainer.getSecretKey());
        System.setProperty("spring.cloud.aws.region.static", localStackContainer.getRegion());
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
}
