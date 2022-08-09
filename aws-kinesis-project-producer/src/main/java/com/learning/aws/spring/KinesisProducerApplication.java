package com.learning.aws.spring;

import com.learning.aws.spring.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({ApplicationProperties.class})
public class KinesisProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KinesisProducerApplication.class, args);
    }
}
