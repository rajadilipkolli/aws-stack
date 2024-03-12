package com.learning.awspring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.awspring.entities.FileInfo;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class FileInfoRepositoryTest {

    @Autowired private TestEntityManager entityManager;

    @Autowired private FileInfoRepository fileInfoRepository;

    @Test
    void testFindByFileName() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("test");
        fileInfo.setFileUrl("testUrl");
        entityManager.persist(fileInfo);
        entityManager.flush();
        List<FileInfo> fileInfoList = fileInfoRepository.findByFileName("test");
        assertThat(fileInfoList).hasSize(1);
    }

    @Test
    void testExistsByFileName() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("test");
        fileInfo.setFileUrl("testUrl");
        entityManager.persist(fileInfo);
        entityManager.flush();
        boolean exists = fileInfoRepository.existsByFileName("test");
        assertThat(exists).isTrue();
    }
}
