package com.example.awsspring.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.VerifyEmailAddressRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class Initializer implements CommandLineRunner {

    private final SesClient sesClient;

    @Override
    public void run(String... args) {
        log.info("Running Initializer.....");
        sesClient.verifyEmailAddress(
                VerifyEmailAddressRequest.builder().emailAddress("sender@example.com").build());
    }
}
