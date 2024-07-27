package com.learning.awssns.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    LocalStackContainer localstackContainer(DynamicPropertyRegistry registry) {
        return new LocalStackContainer(
                DockerImageName.parse("localstack/localstack").withTag("3.6.0"));
    }
}
