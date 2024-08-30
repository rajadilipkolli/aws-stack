package com.learning.awspring;

import com.learning.awspring.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class SQSApplication {

    public static void main(String[] args) {
        SpringApplication.run(SQSApplication.class, args);
    }
}
