package com.learning.awssns.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awssns.common.AbstractIntegrationTest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;

class NotificationMappingControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationMappingController controller;

    @Test
    void handleNotificationMessage_logsMessageAndSubject() throws Exception {
        // Arrange
        byte[] notificationJsonContent = FileCopyUtils.copyToByteArray(Optional.ofNullable(
                        getClass().getClassLoader().getResourceAsStream("notificationMessage.json"))
                .orElseThrow(() -> new IllegalArgumentException("notificationMessage.json not found in classpath")));

        // Act
        mockMvc.perform(post("/testTopic")
                        .header("x-amz-sns-message-type", "Notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notificationJsonContent))
                .andExpect(status().isNoContent());

        // Assert
        assertThat(this.controller.getMessage()).isEqualTo("testMessage");
        assertThat(this.controller.getSubject()).isEqualTo("hello");
    }
}
