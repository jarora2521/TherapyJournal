package com.therapy.nest.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "rabbitmq_messages")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SQLDelete(sql = "UPDATE rabbitmq_messages SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class RabbitMQMessage extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "is_sent", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isSent = Boolean.TRUE;

    @Column(name = "sent_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime sentTime = LocalDateTime.now();

    @Column(name = "is_acknowledged", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isAcknowledged = Boolean.FALSE;

    @Column(name = "acknowledged_time")
    private LocalDateTime acknowledgedTime;

    @Column(name = "error", columnDefinition = "TEXT")
    private String error;

    @Column(name = "number_of_retries")
    private Integer numberOfRetries = 0;
}
