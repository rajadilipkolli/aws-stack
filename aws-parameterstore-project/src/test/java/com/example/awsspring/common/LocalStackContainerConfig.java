package com.example.awsspring.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public class LocalStackContainerConfig {

    @Container
    private static final LocalStackContainer localStackContainer =
            new LocalStackContainer(
                            DockerImageName.parse("localstack/localstack").withTag("4.13.1"))
                    .withCopyFileToContainer(
                            MountableFile.forHostPath("localstack/"),
                            "/etc/localstack/init/ready.d/")
                    .waitingFor(Wait.forLogMessage(".*Initialized\\.\n", 1));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.cloud.aws.endpoint", () -> localStackContainer.getEndpoint().toString());
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
        registry.add("spring.config.import", () -> "aws-parameterstore:/spring/config/");
    }
}
