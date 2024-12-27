package com.learning.awspring;

import com.learning.awspring.common.ContainerConfig;
import org.springframework.boot.SpringApplication;

public class TestS3Application {

    public static void main(String[] args) {
        SpringApplication.from(S3Application::main).with(ContainerConfig.class).run(args);
    }
}
