package com.example.awsspring.common;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public interface DBTestContainer {

    @Container @ServiceConnection
    PostgreSQLContainer sqlContainer =
            new PostgreSQLContainer(DockerImageName.parse("postgres").withTag("18.3-alpine"));
}
