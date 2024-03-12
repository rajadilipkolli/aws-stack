package com.learning.awspring.web.listener;

import com.learning.awspring.entities.InboundLog;
import com.learning.awspring.model.SQSMessagePayload;
import com.learning.awspring.services.InboundLogService;
import com.learning.awspring.utils.AppConstants;
import com.learning.awspring.utils.MessageDeserializationUtil;
import io.awspring.cloud.sqs.annotation.SqsListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SQSListener {

    private final InboundLogService inboundLogService;

    // @SqsListener listens to the message from the specified queue.
    // Here in this example we are printing the message on the console and the message will be
    // deleted from the queue once it is successfully delivered.
    @SqsListener(value = AppConstants.QUEUE)
    public void readMessageFromSqs(List<Message<SQSMessagePayload>> payloadMessageList) {
        log.info("Received messages= {} ", payloadMessageList);

        List<InboundLog> inBoundLogList =
                payloadMessageList.stream()
                        .map(
                                snsMessage -> {
                                    var inboundLog = new InboundLog();
                                    inboundLog.setCreatedDate(LocalDateTime.now(ZoneOffset.UTC));
                                    inboundLog.setMessageId(snsMessage.getHeaders().getId());
                                    inboundLog.setReceivedAt(
                                            LocalDateTime.ofInstant(
                                                    Objects.requireNonNull(
                                                            snsMessage
                                                                    .getHeaders()
                                                                    .get(
                                                                            "Sqs_ReceivedAt",
                                                                            Instant.class)),
                                                    ZoneOffset.UTC));
                                    inboundLog.setReceivedJson(
                                            MessageDeserializationUtil.getMessageBodyAsJson(
                                                    snsMessage.getPayload()));
                                    return inboundLog;
                                })
                        .toList();
        inboundLogService.saveAllMessagesToDatabase(inBoundLogList);
    }
}
