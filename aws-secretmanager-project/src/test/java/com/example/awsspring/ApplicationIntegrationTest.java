package com.example.awsspring;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.awsspring.common.AbstractIntegrationTest;
import com.example.awsspring.config.ApplicationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Autowired private ApplicationProperties applicationProperties;

    @Test
    void contextLoads() {
        assertThat(applicationProperties.getUsername()).isNotNull().isEqualTo("appuser");
        assertThat(applicationProperties.getPassword()).isNotNull().isEqualTo("secret");
    }
}
