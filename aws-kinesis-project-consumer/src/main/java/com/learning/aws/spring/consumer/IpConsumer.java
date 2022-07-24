package com.learning.aws.spring.consumer;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;

@Slf4j
public class IpConsumer {

    @Bean
    public Consumer<String> consumeEvent() {
        return ip -> {
        log.info("IpAddess :{}", ip);
        };
    }
}
