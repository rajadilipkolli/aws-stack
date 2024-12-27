package com.learning.awsspring;

import com.learning.awsspring.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class DynamoDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamoDBApplication.class, args);
    }
}
