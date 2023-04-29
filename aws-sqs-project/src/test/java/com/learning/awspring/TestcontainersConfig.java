package com.learning.awspring;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:15.2-alpine");
    }

    static final LocalStackContainer localStackContainer =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.0.2"))
                    .withServices(SQS);

    static {
        localStackContainer.start();
        System.setProperty(
                "spring.cloud.aws.sqs.endpoint",
                localStackContainer.getEndpointOverride(SQS).toString());
        System.setProperty("spring.cloud.aws.sqs.region", localStackContainer.getRegion());
        System.setProperty(
                "spring.cloud.aws.credentials.access-key", localStackContainer.getAccessKey());
        System.setProperty(
                "spring.cloud.aws.credentials.secret-key", localStackContainer.getSecretKey());
        System.setProperty("spring.cloud.aws.region.static", localStackContainer.getRegion());
    }
}
