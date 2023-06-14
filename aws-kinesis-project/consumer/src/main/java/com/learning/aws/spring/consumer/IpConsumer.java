package com.learning.aws.spring.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.kinesis.model.Record;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IpConsumer {

    private final ObjectMapper objectMapper;

    @Bean
    public Consumer<Flux<List<Record>>> consumeEvent() {
        Consumer<String> onNext =
                ip -> log.info("IpAddess processed at {} and value is:{}", LocalDateTime.now(), ip);
        return recordFlux ->
                recordFlux
                        .flatMap(Flux::fromIterable)
                        .map(
                                record -> {
                                    log.info(
                                            "Sequence Number :{} and partitionKey :{}",
                                            record.sequenceNumber(),
                                            record.partitionKey());
                                    return record.data().asUtf8String();
                                })
                        .doOnNext(onNext)
                        .subscribe();
    }
}
