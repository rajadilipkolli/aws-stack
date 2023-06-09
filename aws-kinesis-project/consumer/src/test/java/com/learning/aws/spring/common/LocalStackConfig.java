package com.learning.aws.spring.common;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

@TestConfiguration(proxyBeanMethods = false)
@Slf4j
public class LocalStackConfig {
    static final LocalStackContainer LOCAL_STACK_CONTAINER =
            new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack").withTag("2.1.0"));

    static {
        LOCAL_STACK_CONTAINER.start();
    }

    @Bean
    @Scheduled(fixedRate = 600000L, initialDelay = 5000)
    public Supplier<List<String>> producerSupplier() {
        return () ->
                IntStream.range(1, 2)
                        .mapToObj(ipSuffix -> "192.168.0." + ipSuffix)
                        .peek(entry -> log.info("sending event {}", entry))
                        .toList();
    }

    @Bean
    @Primary
    public KinesisAsyncClient amazonKinesis() {
        return applyAwsClientOptions(
                KinesisAsyncClient.builder(), LocalStackContainer.Service.KINESIS);
    }

    @Bean
    @Primary
    public DynamoDbAsyncClient amazonDynamoDBAsync() {
        return applyAwsClientOptions(
                DynamoDbAsyncClient.builder(), LocalStackContainer.Service.DYNAMODB);
    }

    @Bean
    @Primary
    public CloudWatchAsyncClient cloudWatchClient() {
        return applyAwsClientOptions(
                CloudWatchAsyncClient.builder(), LocalStackContainer.Service.CLOUDWATCH);
    }

    private static StaticCredentialsProvider getAwsCredentialsProvider() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                        LOCAL_STACK_CONTAINER.getAccessKey(),
                        LOCAL_STACK_CONTAINER.getSecretKey()));
    }

    private static <B extends AwsClientBuilder<B, T>, T> T applyAwsClientOptions(
            B clientBuilder, LocalStackContainer.Service service) {

        return clientBuilder
                .region(Region.of(LOCAL_STACK_CONTAINER.getRegion()))
                .credentialsProvider(getAwsCredentialsProvider())
                .endpointOverride(LOCAL_STACK_CONTAINER.getEndpointOverride(service))
                .build();
    }
}
