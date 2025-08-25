package com.therapy.nest.shared.dto;

import com.therapy.nest.attachment_management.dto.AttachmentDto;
import com.therapy.nest.shared.enums.NotificationStatus;
import com.therapy.nest.shared.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NotificationDto {
    private Long id;
    private String subject;
    private String salutation;
    private String emailTo;
    private String phoneTo;
    private String fullName;
    private String message;
    private String url;
    private NotificationStatus status;
    private NotificationType notificationType;
    private Integer attempts;
    private String remarks;
    private LocalDateTime sentTime;
    private Boolean sentWithAttachment;
    private List<AttachmentDto> attachments;
}
