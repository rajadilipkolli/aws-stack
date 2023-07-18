package com.learning.awsspring.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Slf4j
public class LocalStackConfig {
    protected static final LocalStackContainer LOCAL_STACK_CONTAINER =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack").withTag("2.1.0"))
                    .withCopyFileToContainer(
                            MountableFile.forHostPath(".localstack/"),
                            "/etc/localstack/init/ready.d/")
                    .waitingFor(Wait.forLogMessage(".*Initialized\\.\n", 1));

    static {
        LOCAL_STACK_CONTAINER.start();
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        LOCAL_STACK_CONTAINER.followOutput(logConsumer);
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
                "spring.cloud.aws.endpoint", LOCAL_STACK_CONTAINER::getEndpoint);
    }
}
