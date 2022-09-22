package com.example.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.CLOUDWATCH;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class LocalStackConfig {
    static LocalStackContainer localStackContainer;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.1.0"))
                        .withServices(CLOUDWATCH)
                        .withExposedPorts(4566);
        localStackContainer.start();
    }
}
