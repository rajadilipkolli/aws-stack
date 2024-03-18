package com.learning.aws.spring.entities;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ipaddress_event")
public class IpAddressEvent {

    @Id
    @Column("id")
    private Long id;

    @Column("content")
    private String content;

    @Column("content_created_at")
    private LocalDateTime contentCreatedAt;

    @Column("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column("created_by")
    @CreatedBy
    private String createdBy;

    @Column("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column("updated_by")
    @LastModifiedBy
    private String updatedBy;

    public IpAddressEvent(String content, LocalDateTime contentCreatedAt) {
        this.content = content;
        this.contentCreatedAt = contentCreatedAt;
    }

    public Long getId() {
        return id;
    }

    public IpAddressEvent setId(Long id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public IpAddressEvent setContent(String content) {
        this.content = content;
        return this;
    }

    public LocalDateTime getContentCreatedAt() {
        return contentCreatedAt;
    }

    public IpAddressEvent setContentCreatedAt(LocalDateTime contentCreatedAt) {
        this.contentCreatedAt = contentCreatedAt;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public IpAddressEvent setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public IpAddressEvent setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public IpAddressEvent setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public IpAddressEvent setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IpAddressEvent.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("content='" + content + "'")
                .add("contentCreatedAt=" + contentCreatedAt)
                .add("createdAt=" + createdAt)
                .add("createdBy='" + createdBy + "'")
                .add("updatedAt=" + updatedAt)
                .add("updatedBy='" + updatedBy + "'")
                .toString();
    }
}
