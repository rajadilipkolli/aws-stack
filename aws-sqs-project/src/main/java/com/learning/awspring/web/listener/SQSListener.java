package com.learning.awspring.web.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.awspring.entities.InBoundLog;
import com.learning.awspring.model.SNSMessagePayload;
import com.learning.awspring.repositories.InBoundLogRepository;
import com.learning.awspring.utils.AppConstants;
import com.learning.awspring.utils.MessageDeserializationUtil;
import io.awspring.cloud.sqs.annotation.SqsListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
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
    @SqsListener(value = AppConstants.QUEUE)
    public void readMessageFromSqs(List<Message<SNSMessagePayload>> payloadMessageList) {
        log.info("Received message= {} ", payloadMessageList);

        for (Message<SNSMessagePayload> snsMessagePayload : payloadMessageList) {
            saveMessageToDatabase(
                    snsMessagePayload.getPayload(),
                    Objects.requireNonNull(snsMessagePayload.getHeaders().getId()).toString());
        }
    }

    @Async
    private void saveMessageToDatabase(SNSMessagePayload snsMessagePayload, String messageId) {
        var inboundLog = new InBoundLog();
        inboundLog.setCreatedDate(LocalDateTime.now());
        inboundLog.setMessageId(messageId);
        inboundLog.setReceivedJson(
                MessageDeserializationUtil.getMessageBodyAsJson(snsMessagePayload));
        this.inBoundLogRepository.save(inboundLog);
    }
}
