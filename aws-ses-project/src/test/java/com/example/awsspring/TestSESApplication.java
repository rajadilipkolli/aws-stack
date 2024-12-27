package com.example.awsspring;

import com.example.awsspring.common.LocalStackTestContainer;
import org.springframework.boot.SpringApplication;

public class TestSESApplication {

    public static void main(String[] args) {
        SpringApplication.from(SESApplication::main).with(LocalStackTestContainer.class).run(args);
    }
}
