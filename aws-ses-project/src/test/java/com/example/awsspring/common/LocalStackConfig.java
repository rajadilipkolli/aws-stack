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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class LocalStackConfig {

    static LocalStackContainer localStackContainer;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.14.2"))
                        .withServices(S3, SQS, SES)
                        .withExposedPorts(4566);
        localStackContainer.start();
        try {
            localStackContainer.execInContainer(
                    "awslocal",
                    "ses",
                    "verify-email-identity",
                    "--email-address",
                    "sender@example.com",
                    "--endpoint-url=" + localStackContainer.getEndpointOverride(SES));
        } catch (UnsupportedOperationException | IOException | InterruptedException e) {
            e.printStackTrace();
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
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials("test", "test"));
    }
}
