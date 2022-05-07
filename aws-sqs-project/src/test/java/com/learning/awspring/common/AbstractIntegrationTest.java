package com.learning.awspring.common;

import static com.learning.awspring.utils.AppConstants.PROFILE_IT;
import static com.learning.awspring.utils.AppConstants.PROFILE_TEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.awspring.utils.AppConstants;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles({PROFILE_TEST, PROFILE_IT})
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @Container
    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
                    .withServices(SQS);

    @Container
    private static final PostgreSQLContainer<?> sqlContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("integration-tests-db")
                    .withUsername("username")
                    .withPassword("password");

    static {
        Startables.deepStart(sqlContainer, localStack).join();
    }

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry)
            throws UnsupportedOperationException, IOException, InterruptedException {
        localStack.execInContainer(
                "awslocal", "sqs", "create-queue", "--queue-name", AppConstants.QUEUE);
        registry.add("cloud.aws.sqs.endpoint", () -> localStack.getEndpointOverride(SQS));
        registry.add("cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localStack::getSecretKey);
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @Autowired protected MockMvc mockMvc;

    @Autowired protected ObjectMapper objectMapper;
}
