package com.example.awsspring.service;

import com.example.awsspring.common.AbstractIntegrationTest;
import java.time.Duration;
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

class NotificationServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired private NotificationService notificationService;

    @Test
    void testSendMail() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("sender@example.com");
        simpleMailMessage.setTo("junit@gmail.com");
        simpleMailMessage.setSubject("test subject");
        simpleMailMessage.setText("test text");

        Awaitility.await()
                .atLeast(Duration.ofSeconds(1))
                .atMost(Duration.ofSeconds(60))
                .with()
                .pollInterval(Duration.ofSeconds(1))
                .until(
                        () -> notificationService.sendMailMessage(simpleMailMessage),
                        Matchers.equalTo("sent"));
    }

    @Test
    void testSendMailWithAttachments() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("sender@example.com");
        simpleMailMessage.setTo("rajadilipkolli@gmail.com");
        simpleMailMessage.setSubject("test subject");
        simpleMailMessage.setText("test text");

        notificationService.sendMailMessage(simpleMailMessage);
    }
}
