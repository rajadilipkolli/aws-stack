package com.learning.aws.spring.producer;

import com.learning.aws.spring.model.IpAddressDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class IpProducer {

    @Scheduled(fixedDelay = 3000L)
    @Bean
    public Supplier<List<IpAddressDTO>> producerSupplier() {
        return () ->
                IntStream.range(1, 255)
                        .mapToObj(
                                ipSuffix ->
                                        new IpAddressDTO(
                                                "192.168.0." + ipSuffix, LocalDateTime.now()))
                        .peek(entry -> log.info("sending event {}", entry))
                        .toList();
    }
}
