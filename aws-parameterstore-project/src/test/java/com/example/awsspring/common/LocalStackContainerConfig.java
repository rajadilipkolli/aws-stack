package com.example.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SSM;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class LocalStackContainerConfig {

    private static final LocalStackContainer localStackContainer =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack").withTag("2.0.2"))
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("script.sh", 0775),
                            "/etc/localstack/init/ready.d/")
                    .withServices(SSM)
                    .withEnv("EAGER_SERVICE_LOADING", "1");

    static {
        localStackContainer.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.cloud.aws.parameterstore.endpoint",
                () -> localStackContainer.getEndpointOverride(SSM).toString());
        registry.add("spring.cloud.aws.parameterstore.region", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
    }
}
