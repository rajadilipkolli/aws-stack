package com.learning.awspring.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
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

    @Bean
    LocalStackContainer localstackContainer(DynamicPropertyRegistry registry) {
        LocalStackContainer localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0.2"))
                        .withReuse(true);
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.endpoint", localStackContainer::getEndpoint);
        return localStackContainer;
    }
}
