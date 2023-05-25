package com.example.awsspring;

import com.example.awsspring.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class SESApplication {

    public static void main(String[] args) {
        SpringApplication.run(SESApplication.class, args);
    }
}
