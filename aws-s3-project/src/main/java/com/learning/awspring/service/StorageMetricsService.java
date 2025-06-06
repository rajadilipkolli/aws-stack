package com.learning.awspring.service;

import com.learning.awspring.entities.FileInfo;
import com.learning.awspring.repository.FileInfoRepository;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StorageMetricsService {

    private static final Logger log = LoggerFactory.getLogger(StorageMetricsService.class);
    private static final double BYTES_TO_MB = 1024.0 * 1024.0;

    private final FileInfoRepository fileInfoRepository;

    public StorageMetricsService(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    /**
     * Calculate and return storage metrics for the files stored in S3.
     *
     * @return A map containing storage metrics
     */
    public Map<String, Object> getStorageMetrics() {
        log.debug("Calculating overall storage metrics using database aggregation");

        // Check if any files exist in the repository
        Long totalFileCount = fileInfoRepository.getTotalFileCount();
        if (totalFileCount == null || totalFileCount == 0) {
            log.debug("No files found, returning empty metrics");
            return Collections.emptyMap();
        }

        Map<String, Object> metrics = new HashMap<>();

        // Total file count - using direct query
        metrics.put("totalFileCount", totalFileCount);

        // Total storage used (in bytes) - using direct query
        Long totalStorageBytes = fileInfoRepository.getTotalStorageBytes();
        totalStorageBytes = totalStorageBytes != null ? totalStorageBytes : 0L;
        metrics.put("totalStorageBytes", totalStorageBytes);
        metrics.put(
                "totalStorageMB", totalStorageBytes > 0 ? totalStorageBytes / BYTES_TO_MB : 0.0);

        // Files uploaded in the last 24 hours - using direct query
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        Long recentFileCount = fileInfoRepository.getRecentFileCount(oneDayAgo);
        metrics.put("filesUploadedLast24Hours", recentFileCount != null ? recentFileCount : 0);

        // Content type distribution - using direct query
        Map<String, Long> contentTypeDistribution = new HashMap<>();
        List<Object[]> contentTypeResults = fileInfoRepository.getContentTypeDistribution();
        for (Object[] result : contentTypeResults) {
            if (result[0] != null) {
                contentTypeDistribution.put((String) result[0], (Long) result[1]);
            }
        }
        metrics.put("contentTypeDistribution", contentTypeDistribution);

        // Bucket distribution - using direct query
        Map<String, Long> bucketDistribution = new HashMap<>();
        List<Object[]> bucketResults = fileInfoRepository.getBucketDistribution();
        for (Object[] result : bucketResults) {
            if (result[0] != null) {
                bucketDistribution.put((String) result[0], (Long) result[1]);
            }
        }
        metrics.put("bucketDistribution", bucketDistribution);

        log.debug("Calculated metrics using database aggregation");
        return metrics;
    }

    /**
     * Get file storage metrics for a specific bucket.
     *
     * @param bucketName The name of the bucket
     * @return A map containing storage metrics for the bucket
     */
    public Map<String, Object> getBucketMetrics(String bucketName) {
        log.debug("Calculating bucket metrics for {} using database aggregation", bucketName);
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("bucketName", bucketName);

        // Get file count using direct query
        Long fileCount = fileInfoRepository.getFileCountByBucket(bucketName);
        metrics.put("fileCount", fileCount != null ? fileCount : 0);

        // Get total storage size using direct query
        Long totalBucketStorageBytes = fileInfoRepository.getTotalSizeByBucket(bucketName);
        totalBucketStorageBytes = totalBucketStorageBytes != null ? totalBucketStorageBytes : 0L;
        metrics.put("totalStorageBytes", totalBucketStorageBytes);
        metrics.put(
                "totalStorageMB",
                totalBucketStorageBytes > 0 ? totalBucketStorageBytes / BYTES_TO_MB : 0.0);

        // Get the largest file in bucket using direct query
        List<FileInfo> largestFiles = fileInfoRepository.findLargestFileInBucket(bucketName);
        if (!largestFiles.isEmpty()) {
            FileInfo fileInfo = largestFiles.getFirst();
            Map<String, Object> largestFileInfo = new HashMap<>();
            largestFileInfo.put("fileName", fileInfo.getFileName());
            largestFileInfo.put("fileSize", fileInfo.getFileSize());
            largestFileInfo.put("contentType", fileInfo.getContentType());
            metrics.put("largestFile", largestFileInfo);
        }

        log.debug("Calculated bucket metrics for {} using database aggregation", bucketName);
        return metrics;
    }
}
