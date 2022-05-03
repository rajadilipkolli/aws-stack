package com.learning.awspring;

import com.learning.awspring.config.ApplicationProperties;
import com.learning.awspring.config.AwsS3Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class, AwsS3Config.class})
public class S3Application {

  public static void main(String[] args) {
    SpringApplication.run(S3Application.class, args);
  }
}
