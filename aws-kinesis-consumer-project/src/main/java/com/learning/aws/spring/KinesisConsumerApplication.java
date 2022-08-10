package com.learning.aws.spring;

import com.learning.aws.spring.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class KinesisConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KinesisConsumerApplication.class, args);
    }
}
