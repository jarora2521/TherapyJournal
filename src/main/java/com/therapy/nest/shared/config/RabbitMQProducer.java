package com.therapy.nest.shared.config;

import com.therapy.nest.attachment_management.dto.AttachmentDto;
import com.therapy.nest.shared.dto.NotificationDto;
import com.therapy.nest.shared.entity.Notification;
import com.therapy.nest.shared.entity.NotificationTemplate;
import com.therapy.nest.shared.entity.RabbitMQMessage;
import com.therapy.nest.uaa.entity.UserAccount;
import com.therapy.nest.shared.enums.NotificationCategory;
import com.therapy.nest.shared.enums.NotificationType;
import com.therapy.nest.shared.repository.NotificationRepository;
import com.therapy.nest.shared.repository.NotificationTemplateRepository;
import com.therapy.nest.shared.repository.RabbitMQMessageRepository;
import com.therapy.nest.uaa.repository.UserAccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    @Value("${spring.rabbitmq.template.exchange}")
    private String amqpExchange;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String amqpRoutingKey;

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final UserAccountRepository userAccountRepository;
    private final RabbitMQMessageRepository rabbitMQMessageRepository;

    public boolean sendMessage(Long userId, NotificationType notificationType, NotificationCategory notificationCategory, List<AttachmentDto> attachments) {
        try {
            NotificationTemplate notificationTemplate = notificationTemplateRepository.findByNotificationCategory(notificationCategory).orElse(null);
            if (notificationTemplate == null) {
                logger.error("[RabbitMQProducer][sendMessage] ❌: Notification Template not found");
                return false;
            }

            UserAccount userAccount = userAccountRepository.findById(userId).orElse(null);
            if (userAccount == null) {
                logger.error("[RabbitMQProducer][sendMessage] ❌: User with ID - {} not found", userId);
                return false;
            }

            Notification notification = new Notification();
            notification.setFullName(userAccount.getFullName());
            notification.setEmailTo(userAccount.getEmail());
            notification.setPhoneTo(userAccount.getPhone());
            notification.setNotificationType(notificationType);
            notification.setTitle(notificationTemplate.getTitle());
            notification.setSubject(notificationTemplate.getSubject());
            notification.setSalutation(notificationTemplate.getSalutation());
            notification.setMessage(notificationTemplate.getMessage());
            notification.setSentWithAttachment(!attachments.isEmpty());
            Notification savedNotification = notificationRepository.save(notification);

            NotificationDto notificationDto = new NotificationDto();
            BeanUtils.copyProperties(savedNotification, notificationDto);
            notificationDto.setAttachments(attachments);

            return sendMessageToRabbitMQ(notificationDto);
        } catch (Exception e) {
            logger.error("[RabbitMQProducer][sendMessage] ❌: Exception: {}", e.getMessage());
            return false;
        }
    }

    public boolean sendMessageToRabbitMQ(Object object) {
        String message = null;
        String exception = null;
        boolean isSent = true;

        try {
            message = objectMapper.writeValueAsString(object);
            rabbitTemplate.convertAndSend(amqpExchange, amqpRoutingKey, message);

            Notification notification = objectMapper.readValue(message, Notification.class);
            notificationRepository.save(notification);

            logger.info("[RabbitMQProducer][sendMessageToRabbitMQ] ✅: Successfully sent RabbitMQ Message: {}.", message);
        } catch (Exception e) {
            isSent = false;
            exception = e.getMessage();

            logger.error("[RabbitMQProducer][sendMessageToRabbitMQ] ❌: Exception: {}", exception);
        }

        RabbitMQMessage rabbitMQMessage = new RabbitMQMessage();
        rabbitMQMessage.setPayload(message);
        rabbitMQMessage.setIsSent(isSent);
        rabbitMQMessage.setError(exception);
        rabbitMQMessageRepository.save(rabbitMQMessage);

        return isSent;
    }
}
