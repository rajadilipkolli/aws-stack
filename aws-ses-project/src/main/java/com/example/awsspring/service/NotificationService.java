package com.example.awsspring.service;

import io.awspring.cloud.ses.SimpleEmailServiceJavaMailSender;
import io.awspring.cloud.ses.SimpleEmailServiceMailSender;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.ses.SesClient;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class NotificationService {

    private final SimpleEmailServiceMailSender mailSender;

    private final SimpleEmailServiceJavaMailSender javaMailSender;

    public NotificationService(SesClient sesClient) {
        this.mailSender = new SimpleEmailServiceMailSender(sesClient);
        this.javaMailSender = new SimpleEmailServiceJavaMailSender(sesClient);
    }

    public String sendMailMessage(final SimpleMailMessage simpleMailMessage) {
        log.info("mailSender {}", mailSender.getClass().getName());
        this.mailSender.send(simpleMailMessage);
        return "sent";
    }

    public String sendMailMessageWithAttachments() {
        this.javaMailSender.send(
                mimeMessage -> {
                    MimeMessageHelper helper =
                            new MimeMessageHelper(
                                    mimeMessage, true, StandardCharsets.UTF_8.toString());
                    helper.addTo("foo@bar.com");
                    helper.setFrom("bar@baz.com");

                    InputStreamSource data = new ByteArrayResource("Hello".getBytes());
                    helper.addAttachment("test.txt", data);
                    helper.setSubject("test subject with attachment");
                    helper.setText("mime body", false);
                });
        return "sent";
    }
}
