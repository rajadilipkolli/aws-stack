package com.learning.aws.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.learning.aws.spring.common.AbstractIntegrationTest;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        Long initialCount = ipAddressEventRepository.count().block();
        await().atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofMillis(200))
                .pollDelay(Duration.ofSeconds(3))
                .untilAsserted(
                        () ->
                                assertThat(ipAddressEventRepository.count().block() + initialCount)
                                        .isGreaterThan(10));
    }
}
