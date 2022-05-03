package com.learning.awspring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("config.aws.s3")
public class AwsS3Config {

    private String bucketName;

    private String s3EndpointUrl;
}
