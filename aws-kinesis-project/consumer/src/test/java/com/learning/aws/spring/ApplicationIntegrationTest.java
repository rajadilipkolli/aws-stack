package com.learning.aws.spring;

import com.learning.aws.spring.common.AbstractIntegrationTest;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Test
    void contextLoads() throws InterruptedException {
        // awaiting for consumer to start processing
        TimeUnit.SECONDS.sleep(3);
    }
}
