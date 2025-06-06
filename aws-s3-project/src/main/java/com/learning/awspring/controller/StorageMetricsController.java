package com.learning.awspring.controller;

import com.learning.awspring.service.StorageMetricsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3/metrics")
public class StorageMetricsController {

    private final StorageMetricsService storageMetricsService;

    public StorageMetricsController(StorageMetricsService storageMetricsService) {
        this.storageMetricsService = storageMetricsService;
    }

    /**
     * Get overall storage metrics for all buckets.
     *
     * @return A map of storage metrics
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getStorageMetrics() {
        return ResponseEntity.ok(storageMetricsService.getStorageMetrics());
    }

    /**
     * Get storage metrics for a specific bucket.
     *
     * @param bucketName The name of the bucket to get metrics for
     * @return A map of storage metrics for the bucket
     */
    @GetMapping("/bucket/{bucketName}")
    public ResponseEntity<Map<String, Object>> getBucketMetrics(
            @PathVariable
                    @Valid
                    @Pattern(regexp = "^[a-z0-9.-]+$", message = "Invalid bucket name format")
                    String bucketName) {
        return ResponseEntity.ok(storageMetricsService.getBucketMetrics(bucketName));
    }
}
