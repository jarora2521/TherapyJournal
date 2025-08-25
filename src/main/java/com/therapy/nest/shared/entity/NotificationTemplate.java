package com.therapy.nest.shared.entity;

import com.therapy.nest.shared.enums.NotificationCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@Entity
@Audited
@Table(name = "notification_templates")
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE notification_templates SET deleted = true WHERE id = ?")
public class NotificationTemplate extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "title")
    private String title;

    @Column(name = "subject")
    private String subject;

    @Column(name = "salutation")
    private String salutation;

    @Column(name = "message", columnDefinition = "text", nullable = false)
    private String message;

    @Column(name = "notification_category", nullable = true)
    @Enumerated(EnumType.STRING)
    private NotificationCategory notificationCategory;

    @JsonIgnore
    @OneToMany(mappedBy = "notificationTemplate")
    private Set<Notification> notifications;

}
