package com.learning.awsspring.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class ContainerConfig {

    private static final Logger log = LoggerFactory.getLogger(ContainerConfig.class);

    @Bean
    @ServiceConnection
    LocalStackContainer localstackContainer() {
        LocalStackContainer localstackContainer =
                new LocalStackContainer(
                                DockerImageName.parse("localstack/localstack").withTag("4.10.0"))
                        .withCopyFileToContainer(
                                MountableFile.forHostPath(".localstack/"),
                                "/etc/localstack/init/ready.d/")
                        .waitingFor(Wait.forLogMessage(".*Initialized\\.\n", 1));
        localstackContainer.start();
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        localstackContainer.followOutput(logConsumer);
        return localstackContainer;
    }
}
