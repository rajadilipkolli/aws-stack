package com.example.awsspring.common;

import static org.testcontainers.containers.BindMode.READ_ONLY;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SECRETSMANAGER;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class LocalStackContainerConfig {

    private static final LocalStackContainer localStackContainer =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack").withTag("2.1.0"))
                    .withServices(SECRETSMANAGER)
                    .withClasspathResourceMapping(
                            "/localstack", "/etc/localstack/init/ready.d", READ_ONLY)
                    .waitingFor(Wait.forLogMessage(".*Initialized\\.\n", 1));

    static {
        localStackContainer.start();
        // Workaround to set value early
        System.setProperty(
                "spring.cloud.aws.secretsmanager.endpoint",
                localStackContainer.getEndpointOverride(SECRETSMANAGER).toString());
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.cloud.aws.secretsmanager.endpoint",
                () -> localStackContainer.getEndpointOverride(SECRETSMANAGER).toString());
        registry.add("spring.cloud.aws.secretsmanager.region", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
    }
}
