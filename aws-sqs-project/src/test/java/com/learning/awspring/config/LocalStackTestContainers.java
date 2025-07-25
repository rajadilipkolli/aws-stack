package com.learning.awspring.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class LocalStackTestContainers {

    @Bean
    @ServiceConnection
    LocalStackContainer localstackContainer() {
        return new LocalStackContainer(
                        DockerImageName.parse("localstack/localstack").withTag("4.6.0"))
                .withReuse(true);
    }
}
