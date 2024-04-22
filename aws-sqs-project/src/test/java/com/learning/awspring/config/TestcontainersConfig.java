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
        return new PostgreSQLContainer<>("postgres:16.2-alpine");
    }

    @Bean
    @ServiceConnection
    LocalStackContainer localstackContainer() {
        return new LocalStackContainer(
                        DockerImageName.parse("localstack/localstack").withTag("3.3.0"))
                .withReuse(true);
    }
}
