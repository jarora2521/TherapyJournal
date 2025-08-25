package com.therapy.nest.shared.repository;

import com.therapy.nest.shared.entity.Notification;
import com.therapy.nest.shared.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    Optional<Notification> findFirstByUuid(String uuid);

    Page<Notification> findAllByStatusAndAttemptsLessThanEqual(NotificationStatus notificationStatus, Integer maxSendMailAttempt, Pageable pageable);

    Page<Notification> findAllByStatusAndAttemptsLessThanEqualAndCreatedAtBefore(NotificationStatus notificationStatus, Integer maxSendMailAttempt, LocalDateTime thisInstant, Pageable pageable);
}

