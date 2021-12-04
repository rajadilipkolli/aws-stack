package com.learning.awspring.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static com.learning.awspring.utils.AppConstants.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@ActiveProfiles({PROFILE_TEST, PROFILE_IT})
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

  @Autowired protected MockMvc mockMvc;

  @Autowired protected ObjectMapper objectMapper;

  @Container
  private static final PostgreSQLContainer<?> sqlContainer =
      new PostgreSQLContainer<>("postgres:latest")
          .withDatabaseName("integration-tests-db")
          .withUsername("username")
          .withPassword("password");

  @Container
  private static final LocalStackContainer localStackContainer =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack"), false)
          .withServices(SQS)
          .withExposedPorts(4566);

  static {
    System.setProperty("com.amazonaws.sdk.disableCbor", "true");
    localStackContainer.start();
    sqlContainer.start();
  }

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    localStackContainer.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", QUEUE);
  }

  @DynamicPropertySource
  static void overrideConfiguration(DynamicPropertyRegistry registry) {
    registry.add("cloud.aws.sqs.endpoint", () -> localStackContainer.getEndpointOverride(SQS));
    registry.add("cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
    registry.add("cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
    registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
    registry.add("spring.datasource.username", sqlContainer::getUsername);
    registry.add("spring.datasource.password", sqlContainer::getPassword);
  }
}
