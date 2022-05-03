package com.learning.awspring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("cloud.aws.s3")
public class AwsS3Config {

  private String bucketName;

  private String endpointUrl;

  private String accessKey;

  private String secretKey;
}
