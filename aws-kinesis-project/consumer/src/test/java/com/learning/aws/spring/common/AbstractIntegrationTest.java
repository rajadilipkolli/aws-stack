package com.learning.aws.spring.common;

import static com.learning.aws.spring.utils.AppConstants.PROFILE_TEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.learning.aws.spring.repository.IpAddressEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles({PROFILE_TEST})
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {ContainersConfig.class, ProducerConfig.class})
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest {

    @Autowired protected WebTestClient webTestClient;

    @Autowired protected ObjectMapper objectMapper;

    @Autowired protected IpAddressEventRepository ipAddressEventRepository;
}
