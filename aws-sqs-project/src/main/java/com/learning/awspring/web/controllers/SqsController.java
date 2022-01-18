package com.learning.awspring.web.controllers;

import static com.learning.awspring.utils.AppConstants.QUEUE;

import com.learning.awspring.web.model.Message;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import javax.validation.Valid;
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

  // QueueMessagingTemplate initializes the messaging template by configuring the destination
  // resolver as well as the message converter.
  private final QueueMessagingTemplate queueMessagingTemplate;

  // HTTP POST url - http://localhost:8080/api/sqs/send
  @PostMapping("/send")
  // @ResponseStatus annotation marks the method with the status-code and the reason message that
  // should be returned.
  @ResponseStatus(code = HttpStatus.CREATED)
  public void sendMessageToSqs(@RequestBody @Valid final Message message) {
    log.info("Sending the message to the Amazon sqs.");
    queueMessagingTemplate.convertAndSend(QUEUE, message);
    log.info("Message sent successfully to the Amazon sqs.");
  }
}
