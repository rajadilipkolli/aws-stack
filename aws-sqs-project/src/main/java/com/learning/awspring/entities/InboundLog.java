package com.learning.awspring.entities;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "inbound_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InboundLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "message_id", length = 40)
    @NotBlank(message = "MessageId cannot be blank")
    private String messageId;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", name = "received_json", nullable = false)
    private String receivedJson;

    private LocalDateTime receivedAt;

    @CreatedDate private LocalDateTime createdDate = LocalDateTime.now();

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
