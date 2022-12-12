package com.learning.awspring.entities;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

@Table(name = "INBOUND_LOG")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InBoundLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "message_id", length = 40)
    private String messageId;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", name = "received_json", nullable = false)
    private String receivedJson;

    private LocalDateTime receivedAt;

    @CreatedDate private LocalDateTime createdDate = LocalDateTime.now();
}
