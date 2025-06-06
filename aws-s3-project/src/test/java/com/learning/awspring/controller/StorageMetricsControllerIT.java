package com.learning.awspring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.entities.FileInfo;
import com.learning.awspring.repository.FileInfoRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class StorageMetricsControllerIT extends AbstractIntegrationTest {

    @Autowired private FileInfoRepository fileInfoRepository;

    @BeforeEach
    void setUp() {
        // Delete any existing files
        fileInfoRepository.deleteAll();

        // Create test files with specific values that match the unit test expectations
        List<FileInfo> testFiles = new ArrayList<>();

        // Create 10 files with a total size of 1024 bytes across 2 buckets
        for (int i = 0; i < 10; i++) {
            FileInfo file = new FileInfo();
            file.setFileName("test" + i + ".txt");
            file.setFileUrl(
                    "https://example.com/"
                            + (i < 5 ? "bucket1" : "bucket2")
                            + "/test"
                            + i
                            + ".txt");
            file.setContentType("text/plain");
            file.setFileSize(i < 9 ? 100L : 124L); // Make total exactly 1024 bytes
            file.setBucketName(i < 5 ? "bucket1" : "bucket2"); // Split between 2 buckets
            file.setCreatedAt(LocalDateTime.now());
            file.setUploadSuccessFull(true);
            testFiles.add(file);
        }

        // Save all the test files
        fileInfoRepository.saveAll(testFiles);
    }

    @Test
    void getStorageMetrics_ShouldReturnMetricsFromService() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/s3/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalFileCount").value(10))
                .andExpect(jsonPath("$.totalStorageBytes").value(1024))
                .andExpect(jsonPath("$.totalStorageMB").exists())
                .andExpect(jsonPath("$.contentTypeDistribution").exists())
                .andExpect(jsonPath("$.bucketDistribution.bucket1").value(5))
                .andExpect(jsonPath("$.bucketDistribution.bucket2").value(5));
    }

    @Test
    void getBucketMetrics_ShouldReturnBucketSpecificMetrics() throws Exception {
        // Arrange
        String bucketName = "bucket1";

        // Act & Assert
        mockMvc.perform(get("/s3/metrics/bucket/{bucketName}", bucketName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bucketName").value(bucketName))
                .andExpect(jsonPath("$.fileCount").value(5))
                .andExpect(jsonPath("$.totalStorageBytes").value(500))
                .andExpect(jsonPath("$.totalStorageMB").exists());
    }

    @Test
    void getStorageMetrics_EmptyResponse_ShouldStillReturnOk() throws Exception {
        // Delete all files for this test
        fileInfoRepository.deleteAll();

        // Act & Assert
        mockMvc.perform(get("/s3/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getBucketMetrics_NonexistentBucket_ShouldStillReturnOk() throws Exception {
        // Arrange - bucket exists but has no metrics
        String bucketName = "emptyBucket";

        // Act & Assert
        mockMvc.perform(get("/s3/metrics/bucket/{bucketName}", bucketName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bucketName").value(bucketName))
                .andExpect(jsonPath("$.fileCount").exists())
                .andExpect(jsonPath("$.totalStorageBytes").exists())
                .andExpect(jsonPath("$.totalStorageMB").exists());
    }
}
