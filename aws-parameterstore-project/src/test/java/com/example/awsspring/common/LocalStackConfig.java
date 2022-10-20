package com.example.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SSM;

import java.io.IOException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

public class LocalStackConfig {

    static LocalStackContainer localStackContainer;

    static {
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.2.0"))
                        .withServices(SSM)
                        .withExposedPorts(4566)
                        .withReuse(true);
        localStackContainer.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry)
            throws IOException, InterruptedException {
        localStackContainer.execInContainer(
                "awslocal",
                "ssm",
                "put-parameter",
                "--name",
                "/spring/config/application.username",
                "--value",
                "appuser",
                "--type",
                "String",
                "--region",
                localStackContainer.getRegion());

        registry.add(
                "spring.cloud.aws.parameterstore.endpoint",
                () -> localStackContainer.getEndpointOverride(SSM).toString());
        registry.add("spring.cloud.aws.parameterstore.region", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
    }
}
