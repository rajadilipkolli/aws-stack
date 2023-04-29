package com.learning.aws.spring.consumer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class IpConsumer {

    @Bean
    public Consumer<Flux<List<Record>>> consumeEvent() {
        Consumer<String> onNext =
                ip -> log.info("IpAddess Received at {} is:{}", LocalDateTime.now(), ip);
        return recordFlux ->
                recordFlux
                        .flatMap(Flux::fromIterable)
                        .map(Record::toString)
                        .doOnNext(onNext)
                        .subscribe();
    }
}
