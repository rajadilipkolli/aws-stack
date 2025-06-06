package com.learning.awspring.repository;

import com.learning.awspring.entities.FileInfo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileInfoRepository extends JpaRepository<FileInfo, Integer> {
    List<FileInfo> findByFileName(String name);

    List<FileInfo> findByBucketName(String bucketName);

    boolean existsByFileName(String fileName);

    List<FileInfo> findByContentType(String contentType);

    List<FileInfo> findByCreatedAtAfter(LocalDateTime timestamp);

    @Query("SELECT f FROM FileInfo f WHERE f.fileSize > :minSize")
    List<FileInfo> findLargeFiles(@Param("minSize") Long minSizeBytes);

    @Query("SELECT sum(f.fileSize) FROM FileInfo f WHERE f.bucketName = :bucketName")
    Long getTotalSizeByBucket(@Param("bucketName") String bucketName);

    @Query("SELECT COUNT(f) FROM FileInfo f")
    Long getTotalFileCount();

    @Query("SELECT SUM(f.fileSize) FROM FileInfo f")
    Long getTotalStorageBytes();

    @Query("SELECT COUNT(f) FROM FileInfo f WHERE f.createdAt > :since")
    Long getRecentFileCount(@Param("since") LocalDateTime since);

    @Query("SELECT f.contentType, COUNT(f) FROM FileInfo f GROUP BY f.contentType")
    List<Object[]> getContentTypeDistribution();

    @Query("SELECT f.bucketName, COUNT(f) FROM FileInfo f GROUP BY f.bucketName")
    List<Object[]> getBucketDistribution();

    @Query("SELECT COUNT(f) FROM FileInfo f WHERE f.bucketName = :bucketName")
    Long getFileCountByBucket(@Param("bucketName") String bucketName);

    @Query(
            "SELECT f FROM FileInfo f WHERE f.bucketName = :bucketName AND f.fileSize = (SELECT MAX(ff.fileSize) FROM FileInfo ff WHERE ff.bucketName = :bucketName)")
    List<FileInfo> findLargestFileInBucket(@Param("bucketName") String bucketName);
}
