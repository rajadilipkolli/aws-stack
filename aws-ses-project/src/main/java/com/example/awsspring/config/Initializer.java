package com.example.awsspring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.VerifyEmailAddressRequest;

@Component
public class Initializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Initializer.class);

    private final SesClient sesClient;

    public Initializer(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    @Override
    public void run(String... args) {
        log.info("Running Initializer.....");
        sesClient.verifyEmailAddress(
                VerifyEmailAddressRequest.builder().emailAddress("sender@example.com").build());
        sesClient.verifyEmailAddress(
                VerifyEmailAddressRequest.builder().emailAddress("bar@baz.com").build());
        log.info("Verified emailId.....");
    }
}
