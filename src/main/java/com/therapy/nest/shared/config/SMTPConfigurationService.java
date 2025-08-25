package com.therapy.nest.shared.config;

import com.therapy.nest.attachment_management.dto.AttachmentDto;
import com.therapy.nest.shared.dto.NotificationDto;
import com.therapy.nest.shared.entity.Notification;
import com.therapy.nest.shared.entity.SMTPConfiguration;
import com.therapy.nest.shared.enums.NotificationStatus;
import com.therapy.nest.shared.repository.NotificationRepository;
import com.therapy.nest.shared.repository.SMTPConfigurationRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Properties;


@Service
@RequiredArgsConstructor
public class SMTPConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(SMTPConfigurationService.class);

    private final SMTPConfigurationRepository smtpConfigurationRepository;
    private final NotificationRepository notificationRepository;
    private final TemplateEngine templateEngine;

    @Value("${file.upload-dir}")
    private String UPLOADS_FOLDER;

    @Value("${spring.mail.institution-name}")
    private String mailInstitutionName;

    @Value("${spring.mail.system-name}")
    private String mailSystemName;

    public void sendEmail(NotificationDto notificationDto) {
        try {
            if (notificationDto.getStatus() == NotificationStatus.SENT) {
                logger.info("[SendEmail]: Email notification to {} has already been sent at: {}", notificationDto.getEmailTo(), notificationDto.getSentTime());
                return;
            }

            logger.info("Begin sending email to {}", notificationDto.getEmailTo());

            SMTPConfiguration smtpConfiguration = smtpConfigurationRepository.findFirstByDefaultConfigTrue().orElse(null);
            if (smtpConfiguration == null) {
                logger.error("[SendEmail] ❌: Default SMTP configuration not found");
                return;
            }

            final Context context = new Context();
            context.setVariable("salutation", notificationDto.getSalutation() != null ? notificationDto.getSalutation() : "Dear ");
            context.setVariable("fullName", notificationDto.getFullName());
            context.setVariable("msg", notificationDto.getMessage());
            context.setVariable("url", notificationDto.getUrl());
            context.setVariable("systemName", mailSystemName);
            context.setVariable("hostInstitution", mailInstitutionName);
            String mailBody = templateEngine.process("general-mail", context);

            JavaMailSender mailSender = createJavaMailSender(smtpConfiguration);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(smtpConfiguration.getUsername(), smtpConfiguration.getSenderName()));
            helper.setTo(notificationDto.getEmailTo());
            helper.setSubject(notificationDto.getSubject());
            helper.setText(mailBody, true);

            if (notificationDto.getSentWithAttachment()) {
                for (AttachmentDto attachmentDto : notificationDto.getAttachments()) {
                    FileSystemResource file = new FileSystemResource(UPLOADS_FOLDER + attachmentDto.getPath());
                    if (file.exists()) {
                        helper.addAttachment(attachmentDto.getName(), file);
                    }
                }
            }

            mailSender.send(message);

            notificationDto.setStatus(NotificationStatus.SENT);
            notificationDto.setRemarks(NotificationStatus.SENT.name());

            logger.info("[SendEmail] ✅: Email sent successfully to - {}", notificationDto.getEmailTo());
        } catch (Exception e) {
            notificationDto.setStatus(NotificationStatus.FAILED);
            notificationDto.setRemarks(e.getMessage());
            notificationDto.setAttempts(notificationDto.getAttempts() + 1);

            logger.error("[SendEmail] ❌: Exception -> {}", e.getMessage());
            e.printStackTrace();
        }

        notificationRepository.findById(notificationDto.getId())
                .map(existingNotification -> {
                    BeanUtils.copyProperties(notificationDto, existingNotification);
                    return notificationRepository.save(existingNotification);
                })
                .orElseGet(() -> {
                    Notification newNotification = new Notification();
                    BeanUtils.copyProperties(notificationDto, newNotification);
                    return notificationRepository.save(newNotification);
                });
    }

    public void sendSms(NotificationDto notificationDto) {
        try {
            System.out.println(notificationDto);
        } catch (Exception e) {
            logger.error("[SendEmailAsync] ❌: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public JavaMailSender createJavaMailSender(SMTPConfiguration mailConfiguration) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailConfiguration.getHost());
        mailSender.setPort(Integer.parseInt(mailConfiguration.getPort()));
        mailSender.setUsername(mailConfiguration.getUsername());
        mailSender.setPassword(mailConfiguration.getPassword());
        mailSender.setProtocol(mailConfiguration.getMailProtocol() != null ? mailConfiguration.getMailProtocol() : "smtp");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", mailConfiguration.getAuthRequired());
        props.put("mail.smtp.starttls.required", mailConfiguration.getStarttlsRequired());
        props.put("mail.smtp.starttls.enable", mailConfiguration.getStarttlsEnabled());
        props.put("mail.smtp.ssl.enable", mailConfiguration.getSslEnabled());
        props.put("mail.smtp.ssl.trust", mailConfiguration.getSslTrust());
        props.put("mail.smtp.ssl.checkserveridentity", mailConfiguration.getSslCheckServerIdentity());

        return mailSender;
    }

}
