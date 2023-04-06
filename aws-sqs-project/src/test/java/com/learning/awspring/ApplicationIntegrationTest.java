package com.learning.awspring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.model.SQSMessagePayload;
import com.learning.awspring.repositories.InboundLogRepository;
import com.learning.awspring.utils.FakeObjectCreator;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.Duration;

@TestInstance(Lifecycle.PER_CLASS)
class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Autowired private InboundLogRepository inBoundLogRepository;

    @Test
    void sendingMessage() throws Exception {
        SQSMessagePayload snsMessagePayload = FakeObjectCreator.createMessage();
        long count = this.inBoundLogRepository.count();
        this.mockMvc
                .perform(
                        post("/api/sqs/send")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(snsMessagePayload)))
                .andExpect(status().isCreated());

        Awaitility.given()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(3))
                .await()
                .untilAsserted(
                        () -> assertThat(this.inBoundLogRepository.count()).isEqualTo(count + 1));
    }
}
