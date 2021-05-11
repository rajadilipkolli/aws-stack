package com.learning.awspring.web;

import com.learning.awspring.utils.AppConstants;
import com.learning.awspring.web.model.Message;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SQSListener {

    // @SqsListener listens to the message from the specified queue.
    // Here in this example we are printing the message on the console and the message will be
    // deleted from the queue once it is successfully delivered.
    @SqsListener(value = AppConstants.QUEUE, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void readMessageFromSqs(@Valid Message message, @Header("MessageId") String messageId) {
        log.info("Received message= {} with messageId= {}", message, messageId);
    }
}
