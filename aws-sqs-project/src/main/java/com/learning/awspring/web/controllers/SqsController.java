package com.learning.awspring.web.controllers;

import static com.learning.awspring.utils.AppConstants.QUEUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.awspring.model.SNSMessagePayload;
import com.learning.awspring.utils.MessageDeserializationUtil;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sqs")
@Slf4j
@RequiredArgsConstructor
public class SqsController {

    private final SqsTemplate sqsTemplate;

    private final ObjectMapper objectMapper;

    // HTTP POST url - http://localhost:8080/api/sqs/send
    @PostMapping("/send")
    // @ResponseStatus annotation marks the method with the status-code and the reason message that
    // should be returned.
    @ResponseStatus(code = HttpStatus.CREATED)
    public SendResult<Object> sendMessageToSqs(
            @RequestBody @Valid final SNSMessagePayload snsMessagePayload) {
        log.info("Sending the message to the Amazon sqs.");
        return this.sqsTemplate.send(
                to ->
                        to.queue(QUEUE)
                                .payload(
                                        MessageDeserializationUtil.getMessageBodyAsJson(
                                                snsMessagePayload))
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
    }
}
