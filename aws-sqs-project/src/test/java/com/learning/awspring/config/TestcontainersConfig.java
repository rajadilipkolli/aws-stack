package com.learning.awspring.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:16.1-alpine");
    }

    static final LocalStackContainer localStackContainer =
            new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack").withTag("3.0.2"));

    static {
        localStackContainer.start();
        System.setProperty(
                "spring.cloud.aws.endpoint", localStackContainer.getEndpoint().toString());
        System.setProperty(
                "spring.cloud.aws.credentials.access-key", localStackContainer.getAccessKey());
        System.setProperty(
                "spring.cloud.aws.credentials.secret-key", localStackContainer.getSecretKey());
        System.setProperty("spring.cloud.aws.region.static", localStackContainer.getRegion());
    }
}
