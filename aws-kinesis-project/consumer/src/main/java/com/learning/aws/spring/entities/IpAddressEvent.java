package com.learning.aws.spring.entities;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("ipaddress_event")
@ToString
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
}
