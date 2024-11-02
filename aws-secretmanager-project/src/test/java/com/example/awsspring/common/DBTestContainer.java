package com.example.awsspring.common;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public interface DBTestContainer {

    @Container @ServiceConnection
    PostgreSQLContainer<?> sqlContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag("17.0-alpine"));
}
