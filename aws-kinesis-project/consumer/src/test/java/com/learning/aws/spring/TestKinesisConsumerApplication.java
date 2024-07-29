package com.learning.aws.spring;

import com.learning.aws.spring.common.LocalStackConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
@Import(LocalStackConfig.class)
public class TestKinesisConsumerApplication {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:16.3-alpine");
    }

    public static void main(String[] args) {
        SpringApplication.from(KinesisConsumerApplication::main)
                .with(TestKinesisConsumerApplication.class)
                .run(args);
    }
}
