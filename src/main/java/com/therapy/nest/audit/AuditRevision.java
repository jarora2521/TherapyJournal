package com.therapy.nest.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@RevisionEntity(AuditRevisionListener.class)
@Table(name = "revinfo", schema = "audit")
public class AuditRevision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @RevisionNumber
    @Column(name = "rev")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rev;

    @RevisionTimestamp
    @Column(name = "rev_timestamp")
    private long timestamp;

    @Column(name = "rev_user")
    private String username;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @CreationTimestamp
    @Column(name = "event_time")
    private LocalDateTime eventTime;
}
