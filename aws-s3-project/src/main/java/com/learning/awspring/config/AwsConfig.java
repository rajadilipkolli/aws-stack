package com.learning.awspring.config;

import static com.learning.awspring.utils.AppConstants.PROFILE_PROD;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile({PROFILE_PROD})
public class AwsConfig {

    @Bean
    @Primary
    public AmazonS3 amazonS3Client() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        return builder.build();
    }
}
