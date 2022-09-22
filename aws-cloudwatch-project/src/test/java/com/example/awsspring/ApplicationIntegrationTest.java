package com.example.awsspring;

import com.example.awsspring.common.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Autowired private CloudWatchAsyncClient cloudWatchAsyncClient;

    @LocalServerPort private int localPort;

    @Test
    void contextLoads() {}
}
