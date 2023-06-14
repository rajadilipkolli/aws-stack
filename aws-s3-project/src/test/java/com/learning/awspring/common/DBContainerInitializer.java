package com.learning.awspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

class DBContainerInitializer {

    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:15.3-alpine")
                    .withDatabaseName("integration-tests-db")
                    .withUsername("username")
                    .withPassword("password");

    private static final LocalStackContainer LOCAL_STACK_CONTAINER =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack").withTag("2.1.0"))
                    .withServices(S3);

    static {
        Startables.deepStart(LOCAL_STACK_CONTAINER, POSTGRE_SQL_CONTAINER).join();
    }

    @DynamicPropertySource
    public static void registerApplicationValues(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        dynamicPropertyRegistry.add(
                "spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        dynamicPropertyRegistry.add(
                "spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.s3.endpoint",
                () -> LOCAL_STACK_CONTAINER.getEndpointOverride(S3).toString());
        dynamicPropertyRegistry.add("spring.cloud.aws.s3.region", LOCAL_STACK_CONTAINER::getRegion);
    }

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        LOCAL_STACK_CONTAINER.execInContainer(
                "awslocal",
                "s3api",
                "create-bucket",
                "--bucket",
                "testbucket",
                "--region",
                LOCAL_STACK_CONTAINER.getRegion());
    }
}
