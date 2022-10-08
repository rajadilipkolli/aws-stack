package com.learning.aws.spring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.CLOUDWATCH;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.KINESIS;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import java.io.IOException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class LocalStackConfig {
    static LocalStackContainer localStackContainer;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.2.0"))
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
    public AmazonDynamoDBAsync amazonDynamoDBAsync() {

        return AmazonDynamoDBAsyncClientBuilder.standard()
                .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(DYNAMODB))
                .withCredentials(localStackContainer.getDefaultCredentialsProvider())
                .build();
    }

    @Bean
    @Primary
    public AmazonKinesisAsync amazonKinesis() {
        return AmazonKinesisAsyncClientBuilder.standard()
                .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(KINESIS))
                .withCredentials(localStackContainer.getDefaultCredentialsProvider())
                .build();
    }
}
