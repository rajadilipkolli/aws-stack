package com.learning.awspring.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
@Slf4j
public class Initializer implements CommandLineRunner {

  private final ApplicationProperties properties;

  @Override
  public void run(String... args) {
    Assert.isNull(properties.getRegion(), () -> "region should be null");
    log.info("Running Initializer.....");
  }
}
