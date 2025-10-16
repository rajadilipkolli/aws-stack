package com.learning.awssns.controller;

import io.awspring.cloud.sns.annotation.endpoint.NotificationMessageMapping;
import io.awspring.cloud.sns.annotation.endpoint.NotificationSubscriptionMapping;
import io.awspring.cloud.sns.annotation.endpoint.NotificationUnsubscribeConfirmationMapping;
import io.awspring.cloud.sns.annotation.handlers.NotificationMessage;
import io.awspring.cloud.sns.annotation.handlers.NotificationSubject;
import io.awspring.cloud.sns.handlers.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/testTopic")
public class NotificationMappingController {

    private static final Logger log = LoggerFactory.getLogger(NotificationMappingController.class);

    private String subject;

    private String message;

    String getSubject() {
        return this.subject;
    }

    String getMessage() {
        return this.message;
    }

    @NotificationSubscriptionMapping
    public void handleSubscriptionMessage(NotificationStatus status) {
        log.info("confirming subscription");
        status.confirmSubscription();
    }

    @NotificationMessageMapping
    public void handleNotificationMessage(@NotificationSubject String subject, @NotificationMessage String message) {
        this.subject = subject;
        this.message = message;
        log.info("NotificationMessageMapping message is: {}", message);
        log.info("NotificationMessageMapping subject is: {}", subject);
    }

    @NotificationUnsubscribeConfirmationMapping
    public void handleUnsubscribeMessage(NotificationStatus status) {
        status.confirmSubscription();
    }
}
