package com.learning.aws.spring.producer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Component
@Slf4j
public class IpProducer {

    @Scheduled(fixedDelay = 3000L)
    @Bean
    public Supplier<List<String>> produceSupplier() {
        return () ->
                IntStream.range(1, 200)
                        .mapToObj(ipSuffix -> "192.168.0." + ipSuffix)
                        .peek(entry -> log.info("sending event {}", entry))
                        .toList();
    }
}
