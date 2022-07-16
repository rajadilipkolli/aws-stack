package com.learning.aws.spring.config;

import static com.learning.aws.spring.utils.AppConstants.PROFILE_NOT_TEST;

import io.awspring.cloud.autoconfigure.context.properties.AwsCredentialsProperties;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@Profile({PROFILE_NOT_TEST})
@RequiredArgsConstructor
public class AwsConfig {
    private ApplicationProperties properties;
    private AwsCredentialsProperties awsCredentialsProperties;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
    }

    @Bean
    @Primary
    public DynamoDbClient getDynamoDbClient() {

        return DynamoDbClient.builder()
                .endpointOverride(URI.create(properties.getEndpointUri()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        awsCredentialsProperties.getAccessKey(),
                                        awsCredentialsProperties.getSecretKey())))
                .region(Region.of(properties.getRegion()))
                .build();
    }
}
