package com.learning.aws.spring.common;

import com.learning.aws.spring.model.IpAddressDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

@TestConfiguration(proxyBeanMethods = false)
public class ProducerConfig {

    private static final Logger log = LoggerFactory.getLogger(ProducerConfig.class);

    @Bean
    @Scheduled(fixedRate = 6_000_000L)
    Supplier<List<IpAddressDTO>> producerSupplier() {
        return () ->
                IntStream.range(1, 11)
                        .mapToObj(
                                ipSuffix ->
                                        new IpAddressDTO(
                                                "192.168.0." + ipSuffix, LocalDateTime.now()))
                        .peek(entry -> log.info("sending event {}", entry))
                        .toList();
    }
}
