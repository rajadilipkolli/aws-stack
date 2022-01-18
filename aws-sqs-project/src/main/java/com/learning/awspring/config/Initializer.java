package com.learning.awspring.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.learning.awspring.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
@Slf4j
public class Initializer implements CommandLineRunner {

    private final ApplicationProperties properties;
    private final AmazonSQSAsync amazonSQS;

    @Override
    public void run(String... args) {
        Assert.notNull(properties.getRegion(), () -> "application.properties can't be null");
        log.info("Running Initializer.....");
        createQueueIfNotExists();
    }

    private void createQueueIfNotExists() {
        CreateQueueRequest createQueueRequest =
                new CreateQueueRequest(AppConstants.QUEUE)
                        .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            amazonSQS.createQueue(createQueueRequest);
            log.info("Created Queue :{}", AppConstants.QUEUE);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }
    }
}
