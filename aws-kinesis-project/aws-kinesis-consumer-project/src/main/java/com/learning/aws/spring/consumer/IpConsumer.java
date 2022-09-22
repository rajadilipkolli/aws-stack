package com.learning.aws.spring.consumer;

import com.amazonaws.services.kinesis.model.Record;
import java.nio.charset.StandardCharsets;
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
    public Consumer<Flux<List<Record>>> consumeEvent() {
        Consumer<String> onNext =
                ip -> log.info("IpAddess Received at {} is:{}", LocalDateTime.now(), ip);
        return recordFlux ->
                recordFlux
                        .flatMap(Flux::fromIterable)
                        .map(record -> new String(record.getData().array(), StandardCharsets.UTF_8))
                        .doOnNext(onNext)
                        .subscribe();
    }
}
