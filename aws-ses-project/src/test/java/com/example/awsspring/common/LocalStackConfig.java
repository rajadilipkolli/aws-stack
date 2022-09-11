package com.example.awsspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SES;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@TestConfiguration
public class LocalStackConfig {

    static LocalStackContainer localStackContainer;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.1.0"))
                        .withServices(SES)
                        .withExposedPorts(4566);
        localStackContainer.start();
    }

    @Bean
    @Primary
    public SesClient localstackSesClient() {
        AwsCredentialsProvider credentialsProvider =
                () ->
                        AwsBasicCredentials.create(
                                localStackContainer.getAccessKey(),
                                localStackContainer.getSecretKey());

        return SesClient.builder()
                .region(Region.of(localStackContainer.getRegion()))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
