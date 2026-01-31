package com.learning.awspring.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Table(name = "file_info")
@Entity
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(length = 64, nullable = false)
    private String fileName;

    @Column(length = 328, nullable = false)
    private String fileUrl;

    private boolean isUploadSuccessFull;

    @Column(nullable = false)
    private Long fileSize;

    private String contentType;

    @Column(length = 1024)
    private String metadata;

    @Column(nullable = false)
    private String bucketName;

    @CreatedDate private LocalDateTime createdAt;

    @LastModifiedDate private LocalDateTime updatedAt;

    public FileInfo() {}

    public FileInfo(
            String fileName,
            String fileUrl,
            boolean isUploadSuccessFull,
            Long fileSize,
            String contentType,
            String metadata,
            String bucketName) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.isUploadSuccessFull = isUploadSuccessFull;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.metadata = metadata;
        this.bucketName = bucketName;
    }

    public Integer getId() {
        return id;
    }

    public FileInfo setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileInfo setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public FileInfo setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public boolean isUploadSuccessFull() {
        return isUploadSuccessFull;
    }

    public FileInfo setUploadSuccessFull(boolean isUploadSuccessFull) {
        this.isUploadSuccessFull = isUploadSuccessFull;
        return this;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public FileInfo setFileSize(Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public FileInfo setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getMetadata() {
        return metadata;
    }

    public FileInfo setMetadata(String metadata) {
        this.metadata = metadata;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public FileInfo setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public FileInfo setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public FileInfo setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> effectiveOClass =
                o instanceof HibernateProxy hibernateProxy
                        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                        : o.getClass();
        Class<?> thisEffectiveClass =
                this instanceof HibernateProxy hibernateProxy
                        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                        : this.getClass();
        if (thisEffectiveClass != effectiveOClass) {
            return false;
        }
        FileInfo fileInfo = (FileInfo) o;
        return getId() != null && Objects.equals(getId(), fileInfo.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
