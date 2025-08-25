package com.therapy.nest.shared.entity;

import com.therapy.nest.shared.enums.NotificationStatus;
import com.therapy.nest.shared.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Audited
@Table(name = "notifications")
@SQLDelete(sql = "UPDATE notifications SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Notification extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "title")
    private String title;

    @Column(name = "subject")
    private String subject;

    @Column(name = "salutation")
    private String salutation;

    @Column(name = "email_to")
    private String emailTo;

    @Column(name = "phone_to")
    private String phoneTo;

    @Column(name = "full_name")
    private String fullName;

    @Column(columnDefinition = "text", nullable = false)
    private String message;

    @Column(columnDefinition = "text")
    private String url;

    @ManyToOne(optional = true)
    private NotificationTemplate notificationTemplate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.NEW;

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private Integer attempts = 0;

    @Column(columnDefinition = "text")
    private String remarks;

    @Column(name = "sent_with_attachment")
    private Boolean sentWithAttachment = false;

    @Column(name = "sent_time")
    private LocalDateTime sentTime;
}
