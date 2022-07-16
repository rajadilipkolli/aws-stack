package com.learning.aws.spring.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
@Slf4j
public class IpConsumer {

    @StreamListener(Sink.INPUT)
    public void consume(String ip) {
        log.info("IpAddess :{}", ip);
    }
}
