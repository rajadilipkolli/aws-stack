package com.learning.awspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
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
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.1-alpine"));
    }

    static final LocalStackContainer LOCALSTACKCONTAINER =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack").withTag("3.0.0"))
                    .withCopyFileToContainer(
                            MountableFile.forHostPath(".localstack/"),
                            "/etc/localstack/init/ready.d/")
                    .waitingFor(Wait.forLogMessage(".*LocalStack initialized successfully\n", 1));

    static {
        LOCALSTACKCONTAINER.start();
        System.setProperty(
                "spring.cloud.aws.endpoint", LOCALSTACKCONTAINER.getEndpoint().toString());
        System.setProperty(
                "spring.cloud.aws.credentials.access-key", LOCALSTACKCONTAINER.getAccessKey());
        System.setProperty(
                "spring.cloud.aws.credentials.secret-key", LOCALSTACKCONTAINER.getSecretKey());
        System.setProperty("spring.cloud.aws.region.static", LOCALSTACKCONTAINER.getRegion());
    }

    public static void main(String[] args) {
        SpringApplication.from(S3Application::main).with(TestS3Application.class).run(args);
    }
}
