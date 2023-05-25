package com.learning.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

public class LocalStackConfig {
    protected static final LocalStackContainer LOCAL_STACK_CONTAINER;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        LOCAL_STACK_CONTAINER =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack").withTag("2.1.0"))
                        .withServices(DYNAMODB)
                        .withExposedPorts(4566);
        LOCAL_STACK_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setApplicationProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.credentials.access-key", LOCAL_STACK_CONTAINER::getAccessKey);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.credentials.secret-key", LOCAL_STACK_CONTAINER::getSecretKey);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.region.static", LOCAL_STACK_CONTAINER::getRegion);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.dynamodb.region", LOCAL_STACK_CONTAINER::getRegion);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.dynamodb.endpoint",
                () -> LOCAL_STACK_CONTAINER.getEndpointOverride(DYNAMODB));
    }
}
