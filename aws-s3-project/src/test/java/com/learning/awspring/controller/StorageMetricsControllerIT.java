package com.learning.awspring.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awspring.common.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class StorageMetricsControllerIT extends AbstractIntegrationTest {

    @Test
    void getStorageMetrics_ShouldReturnMetricsFromService() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/s3/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalFileCount").value(10))
                .andExpect(jsonPath("$.totalStorageBytes").value(1024))
                .andExpect(jsonPath("$.bucketCount").value(2));
    }

    @Test
    void getBucketMetrics_ShouldReturnBucketSpecificMetrics() throws Exception {
        // Arrange
        String bucketName = "testBucket";

        // Act & Assert
        mockMvc.perform(get("/s3/metrics/bucket/{bucketName}", bucketName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bucketName").value(bucketName))
                .andExpect(jsonPath("$.fileCount").value(5))
                .andExpect(jsonPath("$.totalStorageBytes").value(512))
                .andExpect(jsonPath("$.averageFileSize").value(102.4));
    }

    @Test
    void getStorageMetrics_EmptyResponse_ShouldStillReturnOk() throws Exception {

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
                .andExpect(jsonPath("$.fileCount").value(0))
                .andExpect(jsonPath("$.totalStorageBytes").value(0));
    }
}
