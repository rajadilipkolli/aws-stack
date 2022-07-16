package com.learning.awspring;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.repository.FileInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class S3ApplicationTest extends AbstractIntegrationTest {

    @Autowired FileInfoRepository fileInfoRepository;

    @Test
    void contextLoads() {
        assertThat(fileInfoRepository.existsByFileName("fileName")).isFalse();
    }
}
