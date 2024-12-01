package com.learning.awssns;

import com.learning.awssns.common.ContainersConfig;
import com.learning.awssns.utils.AppConstants;
import org.springframework.boot.SpringApplication;

public class TestSNSApplication {

    public static void main(String[] args) {
        SpringApplication.from(SNSApplication::main)
                .with(ContainersConfig.class)
                .withAdditionalProfiles(AppConstants.PROFILE_LOCAL)
                .run(args);
    }
}
