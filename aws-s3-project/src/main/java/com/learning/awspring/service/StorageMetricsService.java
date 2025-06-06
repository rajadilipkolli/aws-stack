package com.learning.awspring.service;

import com.learning.awspring.entities.FileInfo;
import com.learning.awspring.repository.FileInfoRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
        log.debug("Calculating overall storage metrics");
        Map<String, Object> metrics = new HashMap<>();

        List<FileInfo> allFiles = fileInfoRepository.findAll();

        // Total file count
        metrics.put("totalFileCount", allFiles.size());

        // Total storage used (in bytes)
        long totalStorageBytes =
                allFiles.stream()
                        .filter(f -> f.getFileSize() != null)
                        .mapToLong(FileInfo::getFileSize)
                        .sum();
        metrics.put("totalStorageBytes", totalStorageBytes);
        metrics.put(
                "totalStorageMB", totalStorageBytes > 0 ? totalStorageBytes / BYTES_TO_MB : 0.0);

        // Files uploaded in the last 24 hours
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        long recentFileCount =
                allFiles.stream()
                        .filter(
                                f ->
                                        f.getCreatedAt() != null
                                                && f.getCreatedAt().isAfter(oneDayAgo))
                        .count();
        metrics.put("filesUploadedLast24Hours", recentFileCount);

        // Content type distribution
        Map<String, Long> contentTypeDistribution =
                allFiles.stream()
                        .filter(f -> f.getContentType() != null)
                        .collect(
                                Collectors.groupingBy(
                                        FileInfo::getContentType, Collectors.counting()));
        metrics.put("contentTypeDistribution", contentTypeDistribution);

        // Bucket distribution
        Map<String, Long> bucketDistribution =
                allFiles.stream()
                        .filter(f -> f.getBucketName() != null)
                        .collect(
                                Collectors.groupingBy(
                                        FileInfo::getBucketName, Collectors.counting()));
        metrics.put("bucketDistribution", bucketDistribution);

        log.debug("Calculated metrics for {} files", allFiles.size());
        return metrics;
    }

    /**
     * Get file storage metrics for a specific bucket.
     *
     * @param bucketName The name of the bucket
     * @return A map containing storage metrics for the bucket
     */
    public Map<String, Object> getBucketMetrics(String bucketName) {
        Map<String, Object> metrics = new HashMap<>();

        List<FileInfo> bucketFiles = fileInfoRepository.findByBucketName(bucketName);

        metrics.put("bucketName", bucketName);
        metrics.put("fileCount", bucketFiles.size());

        long totalBucketStorageBytes =
                bucketFiles.stream()
                        .filter(f -> f.getFileSize() != null)
                        .mapToLong(FileInfo::getFileSize)
                        .sum();
        metrics.put("totalStorageBytes", totalBucketStorageBytes);
        metrics.put(
                "totalStorageMB",
                totalBucketStorageBytes > 0 ? totalBucketStorageBytes / BYTES_TO_MB : 0.0);

        // Get the largest file in bucket
        Optional<FileInfo> largestFile =
                bucketFiles.stream()
                        .filter(f -> f.getFileSize() != null)
                        .max(Comparator.comparing(FileInfo::getFileSize));

        largestFile.ifPresent(
                fileInfo -> {
                    Map<String, Object> largestFileInfo = new HashMap<>();
                    largestFileInfo.put("fileName", fileInfo.getFileName());
                    largestFileInfo.put("fileSize", fileInfo.getFileSize());
                    largestFileInfo.put("contentType", fileInfo.getContentType());
                    metrics.put("largestFile", largestFileInfo);
                });

        return metrics;
    }
}
