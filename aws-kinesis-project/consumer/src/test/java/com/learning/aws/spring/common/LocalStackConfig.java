package com.learning.aws.spring.common;

import com.learning.aws.spring.model.IpAddressDTO;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@Slf4j
public class LocalStackConfig {

    @Container
    static LocalStackContainer localStackContainer =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.2.0"));

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
    @Scheduled(fixedRate = 600_000L)
    public Supplier<List<IpAddressDTO>> producerSupplier() {
        return () ->
                IntStream.range(1, 11)
                        .mapToObj(ipSuffix -> new IpAddressDTO("192.168.0." + ipSuffix))
                        .peek(entry -> log.info("sending event {}", entry))
                        .toList();
    }
}
