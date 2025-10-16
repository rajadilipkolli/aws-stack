package com.learning.awspring.entities;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "inbound_logs")
public class InboundLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(columnDefinition = "uuid", nullable = false, name = "message_id", length = 36)
    private UUID messageId;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", name = "received_json", nullable = false)
    @NotBlank(message = "receivedJson cant be Blank") private String receivedJson;

    private LocalDateTime receivedAt;

    @CreatedDate private LocalDateTime createdDate = LocalDateTime.now();

    public InboundLog() {}

    public InboundLog(
            Long id,
            UUID messageId,
            String receivedJson,
            LocalDateTime receivedAt,
            LocalDateTime createdDate) {
        this.id = id;
        this.messageId = messageId;
        this.receivedJson = receivedJson;
        this.receivedAt = receivedAt;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getReceivedJson() {
        return receivedJson;
    }

    public void setReceivedJson(String receivedJson) {
        this.receivedJson = receivedJson;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InboundLog inboundLog = (InboundLog) o;
        return id != null && Objects.equals(id, inboundLog.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
