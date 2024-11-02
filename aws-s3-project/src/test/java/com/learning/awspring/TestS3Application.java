package com.learning.awspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class TestS3Application {

    @Bean
    @ServiceConnection
    @RestartScope
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.0-alpine"))
                .withReuse(true);
    }

    @Bean
    LocalStackContainer localstackContainer(DynamicPropertyRegistry registry) {
        LocalStackContainer localStackContainer =
                new LocalStackContainer(
                                DockerImageName.parse("localstack/localstack").withTag("3.7.2"))
                        .withCopyFileToContainer(
                                MountableFile.forHostPath(".localstack/"),
                                "/etc/localstack/init/ready.d/")
                        .waitingFor(
                                Wait.forLogMessage(".*LocalStack initialized successfully\n", 1));
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.endpoint", localStackContainer::getEndpoint);
        return localStackContainer;
    }

    public static void main(String[] args) {
        SpringApplication.from(S3Application::main).with(TestS3Application.class).run(args);
    }
}
