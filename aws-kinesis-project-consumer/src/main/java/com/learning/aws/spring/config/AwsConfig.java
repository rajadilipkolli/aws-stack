package com.learning.aws.spring.config;

import static com.learning.aws.spring.utils.AppConstants.PROFILE_NOT_TEST;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import io.awspring.cloud.autoconfigure.context.properties.AwsCredentialsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({PROFILE_NOT_TEST})
@RequiredArgsConstructor
public class AwsConfig {

    private final ApplicationProperties properties;
    private final AwsCredentialsProperties awsCredentialsProperties;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
    }

    @Bean
    @Primary
    public AmazonDynamoDBAsync amazonDynamoDBAsync() {

        return AmazonDynamoDBAsyncClientBuilder.standard()
                .withEndpointConfiguration(
                        new EndpointConfiguration(
                                properties.getEndpointUri(), properties.getRegion()))
                .withCredentials(getCredentialsProvider())
                .build();
    }

    @Bean
    @Primary
    public AmazonKinesisAsync amazonKinesis() {
        return AmazonKinesisAsyncClientBuilder.standard()
                .withCredentials(getCredentialsProvider())
                .withEndpointConfiguration(
                        new EndpointConfiguration(
                                properties.getEndpointUri(), properties.getRegion()))
                .build();
    }

    @Bean
    public AmazonCloudWatchAsync amazonCloudWatch() {
        return AmazonCloudWatchAsyncClientBuilder.standard()
                .withEndpointConfiguration(
                        new EndpointConfiguration(
                                properties.getEndpointUri(), properties.getRegion()))
                .withCredentials(getCredentialsProvider())
                .build();
    }

    private AWSCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(getBasicAWSCredentials());
    }

    private AWSCredentials getBasicAWSCredentials() {
        return new BasicAWSCredentials(
                awsCredentialsProperties.getAccessKey(), awsCredentialsProperties.getSecretKey());
    }
}
