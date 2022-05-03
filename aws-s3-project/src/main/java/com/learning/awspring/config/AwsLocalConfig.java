package com.learning.awspring.config;

import static com.learning.awspring.utils.AppConstants.PROFILE_LOCAL;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile(PROFILE_LOCAL)
public class AwsLocalConfig {

  @Value("${config.aws.s3.access-key}")
  private String accessKey;

  @Value("${config.aws.s3.secret-key}")
  private String secretKey;

  @Autowired private ApplicationProperties properties;

  @Bean
  @Primary
  public AmazonS3 amazonS3Client() {
    AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().enablePathStyleAccess();
    builder.withEndpointConfiguration(getEndpointConfiguration());
    builder.withCredentials(getCredentialsProvider());
    return builder.build();
  }

  private AWSCredentialsProvider getCredentialsProvider() {
    return new AWSStaticCredentialsProvider(getBasicAWSCredentials());
  }

  private AWSCredentials getBasicAWSCredentials() {
    return new BasicAWSCredentials(accessKey, secretKey);
  }

  private AwsClientBuilder.EndpointConfiguration getEndpointConfiguration() {
    return new AwsClientBuilder.EndpointConfiguration(
        properties.getEndpointUri(), properties.getRegion());
  }
}
