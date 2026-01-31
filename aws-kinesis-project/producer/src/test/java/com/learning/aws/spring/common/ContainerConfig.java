package com.learning.aws.spring.common;

import java.time.Duration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

/**
 * Test configuration for AWS services using LocalStack. This class centralizes the LocalStack
 * container configuration and provides necessary beans for AWS integration testing.
 */
@TestConfiguration(proxyBeanMethods = false)
public class ContainerConfig {

    @Bean
    @ServiceConnection
    LocalStackContainer localStackContainer() {
        return new LocalStackContainer(
                DockerImageName.parse("localstack/localstack").withTag("4.13.0"));
    }

    @Bean
    KinesisAsyncClient amazonKinesis(LocalStackContainer localStackContainer) {
        return KinesisAsyncClient.builder()
                .endpointOverride(localStackContainer.getEndpoint())
                .region(Region.of(localStackContainer.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        localStackContainer.getAccessKey(),
                                        localStackContainer.getSecretKey())))
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder()
                                .apiCallTimeout(Duration.ofSeconds(10))
                                .retryPolicy(RetryPolicy.builder().numRetries(3).build())
                                .build())
                .build();
    }
}
