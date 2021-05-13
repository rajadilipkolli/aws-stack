package com.learning.awspring.web.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.awspring.entities.InBoundLog;
import com.learning.awspring.repositories.InBoundLogRepository;
import com.learning.awspring.utils.AppConstants;
import com.learning.awspring.web.model.Message;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import java.time.LocalDateTime;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SQSListener {

    private final InBoundLogRepository inBoundLogRepository;
    private final ObjectMapper objectMapper;

    // @SqsListener listens to the message from the specified queue.
    // Here in this example we are printing the message on the console and the message will be
    // deleted from the queue once it is successfully delivered.
    @SqsListener(value = AppConstants.QUEUE, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void readMessageFromSqs(@Valid Message message, @Header("MessageId") String messageId)
            throws JsonProcessingException {
        log.info("Received message= {} with messageId= {}", message, messageId);

        saveMessageToDatabase(message, messageId);
    }

    @Async
    private void saveMessageToDatabase(Message message, String messageId)
            throws JsonProcessingException {
        InBoundLog inboundLog = new InBoundLog();
        inboundLog.setCreatedDate(LocalDateTime.now());
        inboundLog.setMessageId(messageId);
        inboundLog.setReceivedJson(this.objectMapper.writeValueAsString(message));
        this.inBoundLogRepository.save(inboundLog);
    }
}
