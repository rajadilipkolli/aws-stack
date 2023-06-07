package com.example.awsspring.common;

import static org.testcontainers.containers.BindMode.READ_ONLY;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class LocalStackContainerConfig {

    private static final LocalStackContainer localStackContainer =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack").withTag("2.1.0"))
                    .withClasspathResourceMapping(
                            "/localstack", "/etc/localstack/init/ready.d", READ_ONLY)
                    .waitingFor(Wait.forLogMessage(".*Initialized\\.\n", 1));

    static {
        localStackContainer.start();
        // Workaround to set value early
        System.setProperty(
                "spring.cloud.aws.endpoint",
                localStackContainer.getEndpoint().toString());
        System.setProperty(
                "spring.cloud.aws.credentials.access-key", localStackContainer.getAccessKey());
        System.setProperty(
                "spring.cloud.aws.credentials.secret-key", localStackContainer.getSecretKey());
        System.setProperty("spring.cloud.aws.region.static", localStackContainer.getRegion());
    }
}
