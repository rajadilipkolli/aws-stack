package com.learning.aws.spring.consumer;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class IpConsumer {

    @Bean
    public Consumer<String> consumeEvent() {
        return ip -> {
            log.info("IpAddess :{}", ip);
        };
    }
}
