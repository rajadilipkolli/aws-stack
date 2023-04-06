package com.learning.awspring.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.learning.awspring.domain.FileInfo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

@DataJpaTest
public class FileInfoRepositoryTest {

    @Autowired private TestEntityManager entityManager;

    @Autowired private FileInfoRepository fileInfoRepository;

    @Test
    public void testFindByFileName() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("test");
        fileInfo.setFileUrl("testUrl");
        entityManager.persist(fileInfo);
        entityManager.flush();
        List<FileInfo> fileInfoList = fileInfoRepository.findByFileName("test");
        assertEquals(1, fileInfoList.size());
    }

    @Test
    public void testExistsByFileName() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName("test");
        fileInfo.setFileUrl("testUrl");
        entityManager.persist(fileInfo);
        entityManager.flush();
        boolean exists = fileInfoRepository.existsByFileName("test");
        assertTrue(exists);
    }
}
