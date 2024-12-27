package com.learning.awssns;

import com.learning.awssns.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class SNSApplication {

    public static void main(String[] args) {
        SpringApplication.run(SNSApplication.class, args);
    }
}
