package com.learning.aws.spring.common;

import com.learning.aws.spring.model.IpAddressDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

@TestConfiguration(proxyBeanMethods = false)
@Slf4j
public class LocalStackConfig {

    @Bean
    @Scheduled(fixedRate = 6_000_000L)
    public Supplier<List<IpAddressDTO>> producerSupplier() {
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
