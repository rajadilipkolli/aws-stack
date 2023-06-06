package com.learning.aws.spring.producer;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IpProducer {

    @Scheduled(fixedDelay = 3000L)
    @Bean
    public Supplier<List<String>> producerSupplier() {
        return () ->
                IntStream.range(1, 200)
                        .mapToObj(ipSuffix -> "192.168.0." + ipSuffix)
                        .peek(entry -> log.info("sending event {}", entry))
                        .toList();
    }
}
