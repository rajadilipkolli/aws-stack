package com.example.awsspring.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@TestConfiguration(proxyBeanMethods = false)
public class LocalStackTestContainer {

    @Bean
    @ServiceConnection
    LocalStackContainer localStackContainer() {
        LocalStackContainer localStackContainer =
                new LocalStackContainer(
                        DockerImageName.parse("localstack/localstack").withTag("4.9.2"));
        localStackContainer.start();
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        localStackContainer.followOutput(logConsumer);
        return localStackContainer;
    }
}
