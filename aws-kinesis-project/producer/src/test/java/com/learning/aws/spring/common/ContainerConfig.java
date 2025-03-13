package com.learning.aws.spring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.KINESIS;

import java.time.Duration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.localstack.LocalStackContainer;
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
    LocalStackContainer localStackContainer() {
        return new LocalStackContainer(
                DockerImageName.parse("localstack/localstack").withTag("4.2.0"));
    }

    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar(LocalStackContainer localStackContainer) {
        return dynamicPropertyRegistry -> {
            dynamicPropertyRegistry.add(
                    "spring.cloud.aws.endpoint", localStackContainer::getEndpoint);
            dynamicPropertyRegistry.add(
                    "spring.cloud.aws.region.static", localStackContainer::getRegion);
            dynamicPropertyRegistry.add(
                    "spring.cloud.aws.access-key", localStackContainer::getAccessKey);
            dynamicPropertyRegistry.add(
                    "spring.cloud.aws.secret-key", localStackContainer::getSecretKey);
        };
    }

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
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder()
                                .apiCallTimeout(Duration.ofSeconds(10))
                                .retryPolicy(RetryPolicy.builder().numRetries(3).build())
                                .build())
                .build();
    }
}
