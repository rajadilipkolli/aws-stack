package com.example.awsspring.service;

import java.nio.charset.StandardCharsets;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Autowired private MailSender mailSender;

    @Autowired private JavaMailSender javaMailSender;

    public String sendMailMessage(final SimpleMailMessage simpleMailMessage) {
        log.info("mailSender {}", mailSender.getClass().getName());
        this.mailSender.send(simpleMailMessage);
        return "sent";
    }

    public void sendMailMessageWithAttachments() {
        this.javaMailSender.send(
                new MimeMessagePreparator() {

                    @Override
                    public void prepare(MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper helper =
                                new MimeMessageHelper(
                                        mimeMessage, true, StandardCharsets.UTF_8.toString());
                        helper.addTo("foo@bar.com");
                        helper.setFrom("bar@baz.com");

                        InputStreamSource data = new ByteArrayResource("".getBytes());
                        helper.addAttachment("test.txt", data);
                        helper.setSubject("test subject with attachment");
                        helper.setText("mime body", false);
                    }
                });
    }
}
