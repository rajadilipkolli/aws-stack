package com.learning.aws.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.learning.aws.spring.common.AbstractIntegrationTest;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Test
    void contextLoads() throws InterruptedException {
        // awaiting consumer to start processing
        TimeUnit.SECONDS.sleep(3);
        Long initialCount = ipAddressEventRepository.count().block();
        await().atMost(Duration.ofSeconds(30))
                .pollDelay(Duration.ofSeconds(1))
                .untilAsserted(
                        () ->
                                assertThat(ipAddressEventRepository.count().block() + initialCount)
                                        .isGreaterThan(10));
    }
}
