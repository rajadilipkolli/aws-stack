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
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class LocalStackConfig {

    private static final Logger log = LoggerFactory.getLogger(LocalStackConfig.class);

    static LocalStackContainer localStackContainer =
            new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack").withTag("3.5.0"));

    static {
        localStackContainer.start();
        System.setProperty(
                "spring.cloud.aws.endpoint", localStackContainer.getEndpoint().toString());
        System.setProperty("spring.cloud.aws.region.static", localStackContainer.getRegion());
        System.setProperty(
                "spring.cloud.aws.credentials.access-key", localStackContainer.getAccessKey());
        System.setProperty(
                "spring.cloud.aws.credentials.secret-key", localStackContainer.getSecretKey());
    }

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
