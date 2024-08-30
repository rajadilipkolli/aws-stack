package com.learning.awssns;

import com.learning.awssns.common.ContainersConfig;
import org.springframework.boot.SpringApplication;

public class TestSNSApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "local");
        SpringApplication.from(SNSApplication::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}
