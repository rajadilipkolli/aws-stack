package com.learning.awsspring.config;

import static com.learning.awsspring.utils.AppConstants.PROFILE_LOCAL;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration(proxyBeanMethods = false)
@Profile(PROFILE_LOCAL)
@RequiredArgsConstructor
public class AwsLocalConfig {

    private final AwsDynamoDbProperties awsDynamoDbProperties;

    @Bean
    @Primary
    public DynamoDbClient getDynamoDbClient() throws URISyntaxException {

        return DynamoDbClient.builder()
                .endpointOverride(new URI(awsDynamoDbProperties.getEndpoint()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        awsDynamoDbProperties.getAccessKey(),
                                        awsDynamoDbProperties.getSecretKey())))
                .region(Region.of(awsDynamoDbProperties.getRegion()))
                .build();
    }
}
