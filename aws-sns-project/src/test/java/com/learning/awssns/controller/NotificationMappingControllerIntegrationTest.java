package com.learning.awssns.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awssns.common.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
        byte[] notificationJsonContent = FileCopyUtils.copyToByteArray(
                getClass().getClassLoader().getResourceAsStream("notificationMessage.json"));

        // Act
        mockMvc.perform(post("/testTopic")
                        .header("x-amz-sns-message-type", "Notification")
                        .content(notificationJsonContent))
                .andExpect(status().isNoContent());

        // Assert
        assertThat(this.controller.getMessage()).isEqualTo("testMessage");
        assertThat(this.controller.getSubject()).isEqualTo("hello");
    }
}
