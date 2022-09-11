package com.example.awsspring.config;

import static com.example.awsspring.utils.AppConstants.PROFILE_NOT_TEST;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@Profile({PROFILE_NOT_TEST})
@RequiredArgsConstructor
public class AwsConfig {

    private final ApplicationProperties applicationProperties;

    private final String accessKeyId = "test";
    private final String secretAccessKey = "test";

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
    }

    @Bean
    @Primary
    public SesClient amazonSesClient() {
        AwsCredentialsProvider credentialsProvider =
                () -> AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        return SesClient.builder()
                .region(Region.of(applicationProperties.getRegion()))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
