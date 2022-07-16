package com.learning.aws.spring.common;

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
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@TestConfiguration
public class LocalStackConfig {
    static LocalStackContainer localStackContainer;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.0.0"))
                        .withServices(DYNAMODB, KINESIS)
                        .withExposedPorts(4566);
        localStackContainer.start();
    }

    @Bean
    @Primary
    public DynamoDbClient getDynamoDbClient() {

        return DynamoDbClient.builder()
                .endpointOverride(
                        localStackContainer.getEndpointOverride(
                                LocalStackContainer.Service.DYNAMODB))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        localStackContainer.getAccessKey(),
                                        localStackContainer.getSecretKey())))
                .region(Region.of(localStackContainer.getRegion()))
                .build();
    }
}
