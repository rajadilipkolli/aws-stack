package com.example.awsspring.config;

import static com.example.awsspring.utils.AppConstants.PROFILE_LOCAL;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Configuration(proxyBeanMethods = false)
@Profile(PROFILE_LOCAL)
public class AwsConfig {

    private final String accessKeyId = "noop";
    private final String secretAccessKey = "noop";

    private final ApplicationProperties applicationProperties;

    public AwsConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    // This should be autoconfigured , till then hack
    @Bean
    public CloudWatchAsyncClient amazonCloudWatch() {
        AwsCredentialsProvider credentialsProvider =
                () -> AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        return CloudWatchAsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .endpointOverride(URI.create(applicationProperties.getEndpointUri()))
                .region(Region.US_EAST_1)
                .build();
    }
}
