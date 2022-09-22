package com.example.awsspring.common;

import static com.example.awsspring.utils.AppConstants.PROFILE_TEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles({PROFILE_TEST})
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = {
            "spring.cloud.aws.credentials.access-key=noop",
            "spring.cloud.aws.credentials.secret-key=noop",
            "spring.cloud.aws.region.static=us-east-1",
            "management.metrics.export.cloudwatch.namespace=tc-localstack",
            "management.metrics.export.cloudwatch.step=5s",
            "management.metrics.enable.all=false",
            "management.metrics.enable.http=true"
        })
@AutoConfigureMetrics
@ContextConfiguration(initializers = {DBContainerInitializer.class})
@Import(LocalStackConfig.class)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @Autowired protected MockMvc mockMvc;

    @Autowired protected ObjectMapper objectMapper;
}