package com.learning.awspring.entities;

import com.vladmihalcea.hibernate.type.json.JsonType;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Table(name = "INBOUND_LOG")
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InBoundLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inbound_id_generator")
    @SequenceGenerator(
            name = "inbound_id_generator",
            sequenceName = "inbound_log_id_seq",
            allocationSize = 5)
    private Long id;

    @Column(nullable = false, name = "message_id")
    private String messageId;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb", name = "received_json")
    private String receivedJson;

    private LocalDateTime createdDate;
}
