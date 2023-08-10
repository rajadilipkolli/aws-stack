package com.example.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SES;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class LocalStackConfig {

    static final LocalStackContainer localStackContainer =
            new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack").withTag("2.2.0"));

    static {
        localStackContainer.start();
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        localStackContainer.followOutput(logConsumer);
    }

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.region.static", localStackContainer::getRegion);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.endpoint", () -> localStackContainer.getEndpointOverride(SES));
    }
}
