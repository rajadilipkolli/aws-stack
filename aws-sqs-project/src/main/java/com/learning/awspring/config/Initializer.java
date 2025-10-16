package com.learning.awspring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class Initializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Initializer.class);
    private final ApplicationProperties properties;

    public Initializer(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        Assert.isNull(properties.getRegion(), () -> "region should be null");
        log.info("Running Initializer.....");
    }
}
