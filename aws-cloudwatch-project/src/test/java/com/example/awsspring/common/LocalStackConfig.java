package com.example.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.CLOUDWATCH;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

public class LocalStackConfig {

    static LocalStackContainer localStackContainer;

    static {
        localStackContainer =
                new LocalStackContainer(
                                DockerImageName.parse("localstack/localstack").withTag("2.2.0"))
                        .withServices(CLOUDWATCH)
                        .withExposedPorts(4566);
        localStackContainer.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.cloud.aws.cloudwatch.endpoint",
                () -> localStackContainer.getEndpointOverride(CLOUDWATCH).toString());
        registry.add("spring.cloud.aws.cloudwatch.region", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
    }
}
