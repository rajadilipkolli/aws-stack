package com.learning.awspring;

import com.learning.awspring.common.LocalStackContainerConfig;
import com.learning.awspring.common.SQLContainerConfig;
import org.springframework.boot.SpringApplication;

public class TestS3Application {

    public static void main(String[] args) {
        SpringApplication.from(S3Application::main)
                .with(SQLContainerConfig.class, LocalStackContainerConfig.class)
                .run(args);
    }
}
