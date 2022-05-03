package com.learning.awsspring.config;

import static com.learning.awsspring.utils.AppConstants.PROFILE_NOT_TEST;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@Profile({PROFILE_NOT_TEST})
public class AwsConfig {
    @Autowired private ApplicationProperties properties;

    private String accessKeyId ="test";
    private String secretAccessKey ="test";

    @Bean
	public DynamoDbClient getDynamoDbClient() {
		AwsCredentialsProvider credentialsProvider = new AwsCredentialsProvider() {

            @Override
            public AwsCredentials resolveCredentials() {
                return AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            }
            
        };

		return DynamoDbClient.builder().region(Region.US_EAST_1).credentialsProvider(credentialsProvider).build();
	}
	
	@Bean
	public DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
		return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getDynamoDbClient())
                .build();
	}
}
