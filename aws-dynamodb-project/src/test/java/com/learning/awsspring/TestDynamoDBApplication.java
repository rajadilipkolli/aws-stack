package com.learning.awsspring;

import com.learning.awsspring.common.ContainerConfig;
import org.springframework.boot.SpringApplication;

public class TestDynamoDBApplication {

    public static void main(String[] args) {
        SpringApplication.from(DynamoDBApplication::main).with(ContainerConfig.class).run(args);
    }
}
