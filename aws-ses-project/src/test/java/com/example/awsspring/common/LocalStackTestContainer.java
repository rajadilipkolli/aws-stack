package com.example.awsspring.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class LocalStackTestContainer {
    private static final Logger log = LoggerFactory.getLogger(LocalStackTestContainer.class);

    @Bean
    @ServiceConnection
    LocalStackContainer localStackContainer() {
        LocalStackContainer localStackContainer =
                new LocalStackContainer(
                        DockerImageName.parse("localstack/localstack").withTag("4.14.0"));
        localStackContainer.start();
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        localStackContainer.followOutput(logConsumer);
        return localStackContainer;
    }
}
