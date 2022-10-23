package com.example.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SECRETSMANAGER;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class LocalStackConfig {
    static LocalStackContainer localStackContainer;

    static {
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.2.0"))
                        .withServices(SECRETSMANAGER)
                        .withExposedPorts(4566);
        localStackContainer.start();
    }
}
