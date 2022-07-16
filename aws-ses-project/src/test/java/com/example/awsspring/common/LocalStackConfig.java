package com.example.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SES;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@Slf4j
public class LocalStackConfig {

    static LocalStackContainer localStackContainer;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.0.0"))
                        .withServices(S3, SQS, SES)
                        .withExposedPorts(4566);
        localStackContainer.start();
        try {
            localStackContainer.execInContainer(
                    StandardCharsets.UTF_8,
                    "awslocal",
                    "ses",
                    "verify-email-identity",
                    "--email-address",
                    "sender@example.com",
                    "--region",
                    localStackContainer.getRegion(),
                    "--endpoint-url",
                    localStackContainer.getEndpointOverride(SES).toString());
            log.info("verified user");
        } catch (UnsupportedOperationException | IOException | InterruptedException e) {
            e.printStackTrace();
            log.error("unable to verify user ", e);
        }
    }

    @Bean
    @Primary
    public AmazonSimpleEmailService localstackAmazonSimpleEmailService() {

        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(SES))
                .withCredentials(getCredentialsProvider())
                .build();
    }

    private AWSCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                        localStackContainer.getAccessKey(), localStackContainer.getSecretKey()));
    }
}
