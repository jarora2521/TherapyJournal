package com.therapy.nest.shared.repository;

import com.therapy.nest.shared.entity.NotificationTemplate;
import com.therapy.nest.shared.enums.NotificationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long>, JpaSpecificationExecutor<NotificationTemplate> {
    Optional<NotificationTemplate> findByNotificationCategory(NotificationCategory notificationCategory);
}
