package com.learning.awspring.common;

import static com.learning.awspring.utils.AppConstants.PROFILE_IT;
import static com.learning.awspring.utils.AppConstants.PROFILE_TEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles({PROFILE_TEST, PROFILE_IT})
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = {
            "spring.cloud.aws.credentials.access-key=noop",
            "spring.cloud.aws.credentials.secret-key=noop",
            "spring.cloud.aws.region.static=us-east-1"
        })
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest extends DBContainerInitializer {

    @Autowired protected MockMvc mockMvc;

    @Autowired protected ObjectMapper objectMapper;
}
