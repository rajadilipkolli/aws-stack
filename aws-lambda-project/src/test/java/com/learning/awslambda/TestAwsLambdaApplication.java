package com.learning.awslambda;

import com.learning.awslambda.common.ContainersConfig;
import org.springframework.boot.SpringApplication;

public class TestAwsLambdaApplication {

    public static void main(String[] args) {
        SpringApplication.from(AwsLambdaApplication::main)
                .with(ContainersConfig.class)
                .withAdditionalProfiles("local")
                .run(args);
    }
}
