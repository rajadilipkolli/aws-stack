package com.learning.aws.spring;

import com.learning.aws.spring.common.ContainersConfig;
import org.springframework.boot.SpringApplication;

public class TestKinesisConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.from(KinesisConsumerApplication::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}
