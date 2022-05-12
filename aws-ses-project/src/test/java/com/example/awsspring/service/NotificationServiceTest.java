package com.example.awsspring.service;

import com.example.awsspring.common.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

class NotificationServiceTest extends AbstractIntegrationTest {

    @Autowired private NotificationService notificationService;

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    void setUp() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @AfterEach
    void tearDown() throws Exception {}

    @Test
    void testSendMail() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("sender@example.com");
        simpleMailMessage.setTo("rajadilipkolli@gmail.com");
        simpleMailMessage.setSubject("test subject");
        simpleMailMessage.setText("test text");

        notificationService.sendMailMessage(simpleMailMessage);
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
