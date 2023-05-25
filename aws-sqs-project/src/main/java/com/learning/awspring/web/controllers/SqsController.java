package com.learning.awspring.web.controllers;

import static com.learning.awspring.utils.AppConstants.QUEUE;

import com.learning.awspring.model.SQSMessagePayload;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    // HTTP POST url - http://localhost:8080/api/sqs/send
    @PostMapping("/send")
    // @ResponseStatus annotation marks the method with the status-code and the reason message that
    // should be returned.
    @ResponseStatus(code = HttpStatus.CREATED)
    public SendResult<Object> sendMessageToSqs(
            @RequestBody @Valid final SQSMessagePayload sqsMessagePayload) {
        log.info("Sending the message to the Amazon sqs.");
        return this.sqsTemplate.send(to -> to.queue(QUEUE).payload(sqsMessagePayload));
    }
}
