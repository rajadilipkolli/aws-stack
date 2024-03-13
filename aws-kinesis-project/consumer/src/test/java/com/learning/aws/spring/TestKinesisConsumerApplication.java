package com.learning.aws.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class TestKinesisConsumerApplication {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:16.2-alpine");
    }

    @Bean
    LocalStackContainer localStackContainer(DynamicPropertyRegistry dynamicPropertyRegistry) {
        LocalStackContainer localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.2.0"))
                        .withCopyFileToContainer(
                                MountableFile.forHostPath(".localstack/"),
                                "/etc/localstack/init/ready.d/")
                        .waitingFor(
                                Wait.forLogMessage(".*LocalStack initialized successfully\n", 1));
        dynamicPropertyRegistry.add("spring.cloud.aws.endpoint", localStackContainer::getEndpoint);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.region.static", localStackContainer::getRegion);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.access-key", localStackContainer::getAccessKey);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.secret-key", localStackContainer::getSecretKey);
        return localStackContainer;
    }

    public static void main(String[] args) {
        SpringApplication.from(KinesisConsumerApplication::main)
                .with(TestKinesisConsumerApplication.class)
                .run(args);
    }
}
