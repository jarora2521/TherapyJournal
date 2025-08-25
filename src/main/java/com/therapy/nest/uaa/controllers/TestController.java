package com.therapy.nest.uaa.controllers;

import com.therapy.nest.shared.config.RabbitMQProducer;
import com.therapy.nest.attachment_management.dto.AttachmentDto;
import com.therapy.nest.shared.enums.NotificationCategory;
import com.therapy.nest.shared.enums.NotificationType;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@GraphQLApi
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @GraphQLMutation(name = "sendEmailNotification")
    public boolean sendEmailNotification() {

        AttachmentDto attachment = new AttachmentDto();
        attachment.setName("test.png");
        attachment.setPath("2025-03-10/084e1a8bfdbc11ef97b3e906e0427c991741616950680.png");

        List<AttachmentDto> attachments = new ArrayList<>();
        attachments.add(attachment);

        return rabbitMQProducer.sendMessage(1L, NotificationType.EMAIL, NotificationCategory.NormalNotification, attachments);
    }
}
