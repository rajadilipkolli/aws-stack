package com.example.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.CLOUDWATCH;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@TestConfiguration
public class LocalStackConfig {
    static LocalStackContainer localStackContainer;

    static {
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.1.0"))
                        .withServices(CLOUDWATCH)
                        .withExposedPorts(4566);
        localStackContainer.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.cloud.aws.cloudwatch.endpoint",
                () -> localStackContainer.getEndpointOverride(CLOUDWATCH).toString());
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.region.staticRegion", localStackContainer::getRegion);
    }

    @Primary
    @Bean
    public CloudWatchAsyncClient amazonCloudWatch() {
        AwsCredentialsProvider credentialsProvider =
                () ->
                        AwsBasicCredentials.create(
                                localStackContainer.getAccessKey(),
                                localStackContainer.getSecretKey());
        return CloudWatchAsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .endpointOverride(localStackContainer.getEndpointOverride(CLOUDWATCH))
                .region(Region.of(localStackContainer.getRegion()))
                .build();
    }
}
