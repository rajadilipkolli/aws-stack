package com.learning.aws.spring.consumer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class IpConsumer {

    @Bean
    public Consumer<List<String>> consumeEvent() {
        return ipList -> {
            log.info("IpAddess Received at {} is:{}", LocalDateTime.now(),ipList);
        };
    }
}
