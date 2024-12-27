package com.learning.aws.spring.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    LocalStackContainer localStackContainer() {
        return new LocalStackContainer(
                DockerImageName.parse("localstack/localstack").withTag("4.0.3"));
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.2-alpine"));
    }

    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar(LocalStackContainer localStackContainer) {
        return registry -> {
            registry.add("spring.cloud.aws.endpoint", localStackContainer::getEndpoint);
            registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
            registry.add(
                    "spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
            registry.add(
                    "spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        };
    }
}
