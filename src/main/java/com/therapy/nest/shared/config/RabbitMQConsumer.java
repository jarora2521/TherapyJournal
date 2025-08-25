package com.therapy.nest.shared.config;

import com.therapy.nest.shared.dto.NotificationDto;
import com.therapy.nest.shared.enums.NotificationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final SMTPConfigurationService smtpConfigurationService;

    @RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
    public void receiveMessage(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            NotificationDto notification = objectMapper.readValue(message, NotificationDto.class);

            if (notification.getNotificationType() == NotificationType.EMAIL) {
                smtpConfigurationService.sendEmail(notification);
            } else if (notification.getNotificationType() == NotificationType.SMS) {
                smtpConfigurationService.sendSms(notification);
            } else {
                throw new Exception("Invalid Notification Type");
            }
        } catch (Exception e) {
            logger.error("[SendEmailAsync] ❌: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
