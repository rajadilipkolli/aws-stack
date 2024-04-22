package com.learning.awspring.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import org.hibernate.proxy.HibernateProxy;

@Table(name = "file_info")
@Entity
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String fileName;
    private String fileUrl;
    private boolean isUploadSuccessFull;

    public FileInfo() {}

    public FileInfo(String fileName, String fileUrl, boolean isUploadSuccessFull) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.isUploadSuccessFull = isUploadSuccessFull;
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass =
                o instanceof HibernateProxy hibernateProxy
                        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                        : o.getClass();
        Class<?> thisEffectiveClass =
                this instanceof HibernateProxy hibernateProxy
                        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                        : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
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
