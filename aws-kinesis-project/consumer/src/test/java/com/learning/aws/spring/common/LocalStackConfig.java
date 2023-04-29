package com.learning.aws.spring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.CLOUDWATCH;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.KINESIS;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

import java.io.IOException;

@TestConfiguration
public class LocalStackConfig {
    static LocalStackContainer localStackContainer;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.0.2"))
                        .withEnv("EAGER_SERVICE_LOADING", "1")
                        .withServices(CLOUDWATCH, DYNAMODB, KINESIS)
                        .withExposedPorts(4566);
        localStackContainer.start();
        try {
            localStackContainer.execInContainer(
                    "awslocal",
                    "kinesis",
                    "create-stream",
                    "--stream-name",
                    "my-test-stream",
                    "--shard-count",
                    "1");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Primary
    public DynamoDbAsyncClient amazonDynamoDBAsync() {

        return DynamoDbAsyncClient.builder()
                .endpointOverride(localStackContainer.getEndpointOverride(DYNAMODB))
                .region(Region.of(localStackContainer.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        localStackContainer.getAccessKey(),
                                        localStackContainer.getSecretKey())))
                .build();
    }

    @Bean
    @Primary
    public KinesisAsyncClient amazonKinesis() {
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
}
