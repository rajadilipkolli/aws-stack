package com.example.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SES;

import lombok.extern.slf4j.Slf4j;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class LocalStackConfig {

    static LocalStackContainer localStackContainer;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.0.2"))
                        .withServices(SES)
                        .withExposedPorts(4566);
        localStackContainer.start();
    }

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.region.static", localStackContainer::getRegion);
        dynamicPropertyRegistry.add("spring.cloud.aws.ses.region", localStackContainer::getRegion);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.ses.endpoint",
                () -> localStackContainer.getEndpointOverride(SES).toString());
    }
}
