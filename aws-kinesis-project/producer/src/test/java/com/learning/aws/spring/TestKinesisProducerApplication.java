package com.learning.aws.spring;

import com.learning.aws.spring.common.ConsumerConfig;
import com.learning.aws.spring.common.ContainerConfig;
import org.springframework.boot.SpringApplication;

public class TestKinesisProducerApplication {

    public static void main(String[] args) {
        SpringApplication.from(KinesisProducerApplication::main)
                .with(ContainerConfig.class, ConsumerConfig.class)
                .run(args);
    }
}
