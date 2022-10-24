package com.learning.awspring.web.controllers;

import static com.learning.awspring.utils.AppConstants.QUEUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.awspring.model.SNSMessagePayload;
import com.learning.awspring.utils.MessageDeserializationUtil;
import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;

@RestController
@RequestMapping("/api/sqs")
@Slf4j
@RequiredArgsConstructor
public class SqsController {

    // SqsAsyncClient initializes the messaging client by configuring the destination
    // resolver as well as the message converter.
    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;

    // HTTP POST url - http://localhost:8080/api/sqs/send
    @PostMapping("/send")
    // @ResponseStatus annotation marks the method with the status-code and the reason message that
    // should be returned.
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompletableFuture<Void> sendMessageToSqs(
            @RequestBody @Valid final SNSMessagePayload snsMessagePayload) {
        log.info("Sending the message to the Amazon sqs.");
        return this.sqsAsyncClient
                .getQueueUrl(request -> request.queueName(QUEUE))
                .thenApply(GetQueueUrlResponse::queueUrl)
                .thenCompose(queueUrl -> sendToUrl(queueUrl, snsMessagePayload));
    }

    public CompletableFuture<Void> sendToUrl(String queueUrl, Object payload) {
        return this.sqsAsyncClient
                .sendMessage(
                        request ->
                                request.messageBody(
                                                MessageDeserializationUtil.getMessageBodyAsJson(
                                                        payload))
                                        .queueUrl(queueUrl))
                .thenRun(() -> log.info("Message sent successfully to the Amazon sqs."));
    }
}
