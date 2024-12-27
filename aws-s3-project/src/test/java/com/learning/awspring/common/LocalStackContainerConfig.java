package com.learning.awspring.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class LocalStackContainerConfig {

    @Bean
    LocalStackContainer localstackContainer() {
        return new LocalStackContainer(
                        DockerImageName.parse("localstack/localstack").withTag("4.0.3"))
                .withCopyFileToContainer(
                        MountableFile.forHostPath(".localstack/"), "/etc/localstack/init/ready.d/")
                .waitingFor(Wait.forLogMessage(".*LocalStack initialized successfully\n", 1));
    }

    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar(LocalStackContainer localStackContainer) {
        return registry -> {
            registry.add(
                    "spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
            registry.add(
                    "spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
            registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
            registry.add("spring.cloud.aws.endpoint", localStackContainer::getEndpoint);
        };
    }
}
