package com.example.awsspring;

import com.example.awsspring.common.DBTestContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
@ImportTestcontainers(DBTestContainer.class)
public class TestApplication {

    @Bean
    @ServiceConnection
    LocalStackContainer localStackContainer() {
        return new LocalStackContainer(
                        DockerImageName.parse("localstack/localstack").withTag("4.14.0"))
                .withCopyFileToContainer(
                        MountableFile.forHostPath("localstack/"), "/etc/localstack/init/ready.d/")
                .waitingFor(Wait.forLogMessage(".*LocalStack initialized successfully\n", 1));
    }

    public static void main(String[] args) {
        SpringApplication.from(Application::main).with(TestApplication.class).run(args);
    }
}
