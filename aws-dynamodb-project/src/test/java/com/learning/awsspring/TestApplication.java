package com.learning.awsspring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Slf4j
@TestConfiguration(proxyBeanMethods = false)
public class TestApplication {

    @Bean
    @ServiceConnection
    LocalStackContainer localstackContainer() {
        LocalStackContainer localstackContainer =
                new LocalStackContainer(
                                DockerImageName.parse("localstack/localstack").withTag("3.7.2"))
                        .withCopyFileToContainer(
                                MountableFile.forHostPath(".localstack/"),
                                "/etc/localstack/init/ready.d/")
                        .waitingFor(Wait.forLogMessage(".*Initialized\\.\n", 1));
        localstackContainer.start();
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        localstackContainer.followOutput(logConsumer);
        return localstackContainer;
    }

    public static void main(String[] args) {
        SpringApplication.from(Application::main).with(TestApplication.class).run(args);
    }
}
