package com.example.awsspring;

import com.example.awsspring.common.ContainerConfig;
import org.springframework.boot.SpringApplication;

public class TestCloudWatchApplication {

    public static void main(String[] args) {
        SpringApplication.from(CloudWatchApplication::main).with(ContainerConfig.class).run(args);
    }
}
