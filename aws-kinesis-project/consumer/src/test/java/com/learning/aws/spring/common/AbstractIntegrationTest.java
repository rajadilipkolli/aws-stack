package com.learning.aws.spring.common;

import static com.learning.aws.spring.utils.AppConstants.PROFILE_TEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.aws.spring.KinesisConsumerApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles({PROFILE_TEST})
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {KinesisConsumerApplication.class, LocalStackConfig.class})
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest {

    @Autowired protected WebTestClient webTestClient;

    @Autowired protected ObjectMapper objectMapper;
}
