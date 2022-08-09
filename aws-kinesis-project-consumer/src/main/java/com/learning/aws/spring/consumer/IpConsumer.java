package com.learning.aws.spring.consumer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Slf4j
@Configuration
public class IpConsumer {

    @Bean
    public Consumer<Flux<List<String>>> consumeEvent() {
        Consumer<String> onNext =
                ip -> log.info("IpAddess Received at {} is:{}", LocalDateTime.now(), ip);
        return recordFlux -> recordFlux.flatMap(Flux::fromIterable).doOnNext(onNext).subscribe();
    }
}
