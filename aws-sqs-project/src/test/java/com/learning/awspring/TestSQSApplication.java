package com.learning.awspring;

import com.learning.awspring.config.LocalStackTestContainers;
import com.learning.awspring.config.SQLTestcontainersConfig;
import org.springframework.boot.SpringApplication;

public class TestSQSApplication {

    public static void main(String[] args) {
        SpringApplication.from(SQSApplication::main)
                .with(SQLTestcontainersConfig.class, LocalStackTestContainers.class)
                .run(args);
    }
}
