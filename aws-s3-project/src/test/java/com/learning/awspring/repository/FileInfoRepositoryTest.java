package com.learning.awspring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.awspring.common.SQLContainerConfig;
import com.learning.awspring.entities.FileInfo;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(SQLContainerConfig.class)
class FileInfoRepositoryTest {

    @Autowired private TestEntityManager entityManager;

    @Autowired private FileInfoRepository fileInfoRepository;

    private LocalDateTime now;

    @BeforeEach
    void setup() {
        now = LocalDateTime.now();

        // File 1
        FileInfo fileInfo1 = new FileInfo();
        fileInfo1.setFileName("test1");
        fileInfo1.setFileUrl("testUrl1");
        fileInfo1.setFileSize(512L);
        fileInfo1.setBucketName("testBucket1");
        fileInfo1.setContentType("image/jpeg");
        fileInfo1.setCreatedAt(now);
        entityManager.persist(fileInfo1);

        // File 2
        FileInfo fileInfo2 = new FileInfo();
        fileInfo2.setFileName("test2");
        fileInfo2.setFileUrl("testUrl2");
        fileInfo2.setFileSize(1024L);
        fileInfo2.setBucketName("testBucket1");
        fileInfo2.setContentType("application/pdf");
        fileInfo2.setCreatedAt(now.minusDays(2));
        entityManager.persist(fileInfo2);

        // File 3
        FileInfo fileInfo3 = new FileInfo();
        fileInfo3.setFileName("test3");
        fileInfo3.setFileUrl("testUrl3");
        fileInfo3.setFileSize(256L);
        fileInfo3.setBucketName("testBucket2");
        fileInfo3.setContentType("text/plain");
        fileInfo3.setCreatedAt(now);
        entityManager.persist(fileInfo3);

        entityManager.flush();
    }

    @Test
    void testFindByFileName() {
        List<FileInfo> fileInfoList = fileInfoRepository.findByFileName("test1");
        assertThat(fileInfoList).hasSize(1);
        assertThat(fileInfoList.getFirst().getFileName()).isEqualTo("test1");
    }

    @Test
    void testExistsByFileName() {
        boolean exists = fileInfoRepository.existsByFileName("test1");
        assertThat(exists).isTrue();

        exists = fileInfoRepository.existsByFileName("nonExistent");
        assertThat(exists).isFalse();
    }

    @Test
    void testGetTotalFileCount() {
        Long count = fileInfoRepository.getTotalFileCount();
        assertThat(count).isEqualTo(3);
    }

    @Test
    void testGetTotalStorageBytes() {
        Long totalBytes = fileInfoRepository.getTotalStorageBytes();
        assertThat(totalBytes).isEqualTo(1792L); // 512 + 1024 + 256
    }

    @Test
    void testGetRecentFileCount() {
        Long recentCount = fileInfoRepository.getRecentFileCount(now.minusDays(1));
        assertThat(recentCount).isEqualTo(2); // Only files created within the last day
    }

    @Test
    void testGetContentTypeDistribution() {
        List<Object[]> distribution = fileInfoRepository.getContentTypeDistribution();
        assertThat(distribution).hasSize(3);

        // Convert to a more easily testable format
        boolean foundJpeg = false;
        boolean foundPdf = false;
        boolean foundText = false;

        for (Object[] result : distribution) {
            String contentType = (String) result[0];
            Long count = (Long) result[1];

            if ("image/jpeg".equals(contentType)) {
                assertThat(count).isEqualTo(1L);
                foundJpeg = true;
            } else if ("application/pdf".equals(contentType)) {
                assertThat(count).isEqualTo(1L);
                foundPdf = true;
            } else if ("text/plain".equals(contentType)) {
                assertThat(count).isEqualTo(1L);
                foundText = true;
            }
        }

        assertThat(foundJpeg && foundPdf && foundText).isTrue();
    }

    @Test
    void testGetBucketDistribution() {
        List<Object[]> distribution = fileInfoRepository.getBucketDistribution();
        assertThat(distribution).hasSize(2);

        boolean foundBucket1 = false;
        boolean foundBucket2 = false;

        for (Object[] result : distribution) {
            String bucketName = (String) result[0];
            Long count = (Long) result[1];

            if ("testBucket1".equals(bucketName)) {
                assertThat(count).isEqualTo(2L);
                foundBucket1 = true;
            } else if ("testBucket2".equals(bucketName)) {
                assertThat(count).isEqualTo(1L);
                foundBucket2 = true;
            }
        }

        assertThat(foundBucket1 && foundBucket2).isTrue();
    }

    @Test
    void testGetFileCountByBucket() {
        Long count = fileInfoRepository.getFileCountByBucket("testBucket1");
        assertThat(count).isEqualTo(2L);

        count = fileInfoRepository.getFileCountByBucket("testBucket2");
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void testGetTotalSizeByBucket() {
        Long totalSize = fileInfoRepository.getTotalSizeByBucket("testBucket1");
        assertThat(totalSize).isEqualTo(1536L); // 512 + 1024

        totalSize = fileInfoRepository.getTotalSizeByBucket("testBucket2");
        assertThat(totalSize).isEqualTo(256L);
    }

    @Test
    void testFindLargestFileInBucket() {
        List<FileInfo> largestFiles = fileInfoRepository.findLargestFileInBucket("testBucket1");
        assertThat(largestFiles).hasSize(1);
        assertThat(largestFiles.getFirst().getFileName()).isEqualTo("test2");
        assertThat(largestFiles.getFirst().getFileSize()).isEqualTo(1024L);
    }
}
