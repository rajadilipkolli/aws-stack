package com.learning.awsspring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("cloud.aws.dynamodb")
public class AwsDynamoDbProperties {

    private String accessKey;

    private String secretKey;

    private String region;

    private String endpoint;
}
