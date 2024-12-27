package com.learning.awspring.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class LocalStackContainerConfig {

    @Bean
    @ServiceConnection
    LocalStackContainer localstackContainer() {
        return new LocalStackContainer(
                        DockerImageName.parse("localstack/localstack").withTag("4.0.3"))
                .withCopyFileToContainer(
                        MountableFile.forHostPath(".localstack/"), "/etc/localstack/init/ready.d/")
                .waitingFor(Wait.forLogMessage(".*LocalStack initialized successfully\n", 1));
    }
}
