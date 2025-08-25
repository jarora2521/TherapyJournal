package com.therapy.nest.shared.utils;

import com.therapy.nest.uaa.dto.Oauth2RegisteredClientDto;
import com.therapy.nest.uaa.dto.PermissionDto;
import com.therapy.nest.uaa.dto.UserDto;
import com.therapy.nest.shared.entity.SMTPConfiguration;
import com.therapy.nest.shared.repository.GeneralConfigurationRepository;
import com.therapy.nest.shared.repository.NotificationTemplateRepository;
import com.therapy.nest.shared.repository.SMTPConfigurationRepository;
import com.therapy.nest.uaa.service.RoleService;
import com.therapy.nest.uaa.service.UserAccountService;
import com.therapy.nest.uaa.service_implementation.Oauth2RegisteredClientService;
import com.therapy.nest.uaa.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class Initializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private Oauth2RegisteredClientService oauth2RegisteredClientService;

    @Autowired
    private GeneralConfigurationRepository generalConfigurationRepository;

    @Autowired
    private SMTPConfigurationRepository smtpConfigurationRepository;

    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.first.name}")
    private String adminFirstName;

    @Value("${admin.middle.name}")
    private String adminMiddleName;

    @Value("${admin.last.name}")
    private String adminLastName;

    @Value("${admin.phone-number}")
    private String adminPhoneNumber;

    @Value("${oauth2.client-id}")
    private String oauth2ClientId;

    @Value("${oauth2.client-name}")
    private String oauth2ClientName;

    @Value("${oauth2.client-secret}")
    private String oauth2ClientSecret;

    @Value("${oauth2.callback-uri}")
    private String oauth2CallbackUri;

    @Value("${oauth2.grant-type}")
    private String oauth2GrantType;

    @Value("${oauth2.client-auth-method}")
    private String oauth2ClientAuthMethod;

    @Value("${oauth2.token-format}")
    private String oauth2TokenFormat;

    @Value("${oauth2.token-required-proof-key:false}")
    private String oauth2TokenRequireProofKey;

    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${spring.mail.port}")
    private String smtpPort;

    @Value("${spring.mail.username}")
    private String smtpUser;

    @Value("${spring.mail.password}")
    private String smtpPasswd;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String smtpAuthRequired;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String smtpEnableTls;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("************** START INITIALIZING DATA ****************");

        logger.info("********** Seeding Role Data **********");
        List<PermissionDto> permissionDtos = getPermissionDtos();
//        Role role = roleService.seedSuperAdminRole(permissionDtos);
        logger.info("********** Finished Seeding Role Data **********");

        logger.info("********** Seeding UserAccount Data **********");
        UserDto userDto = new UserDto();
        userDto.setEmail(adminEmail);
        userDto.setFirstName(adminFirstName);
        userDto.setMiddleName(adminMiddleName);
        userDto.setLastName(adminLastName);
        userDto.setPhone(adminPhoneNumber);
        userDto.setPassword(adminPassword);
        userDto.setAuthenticationMethod(AuthenticationMethod.DATABASE);
//        userAccountService.createSuperAdminAccount(userDto, role);
        logger.info("********** Finished Seeding UserAccount Data **********");

        logger.info("********** Seeding OAuth2 Client Data **********");
        Oauth2RegisteredClientDto clientDto = new Oauth2RegisteredClientDto();
        clientDto.setClientId(oauth2ClientId);
        clientDto.setClientName(oauth2ClientName);
        clientDto.setClientSecret(oauth2ClientSecret);
        clientDto.setGrantType(Oauth2GrantType.valueOf(oauth2GrantType));
        clientDto.setRedirectUri(oauth2CallbackUri);
        clientDto.setClientAuthMethod(Oauth2ClientAuthMethod.valueOf(oauth2ClientAuthMethod));
        clientDto.setAccessTokenDuration(TokenDuration.HOURS);
        clientDto.setAccessTokenTimeToLive(3L);
        clientDto.setRefreshTokenDuration(TokenDuration.MINUTES);
        clientDto.setRefreshTokenTimeToLive(30L);
        clientDto.setAuthorizationCodeDuration(TokenDuration.MINUTES);
        clientDto.setAuthorizationCodeTimeToLive(5L);
        clientDto.setTokenFormat(Oauth2TokenFormat.valueOf(oauth2TokenFormat));
        clientDto.setRequiredProofKey(Boolean.parseBoolean(oauth2TokenRequireProofKey));
//        oauth2RegisteredClientService.createOrUpdateOauth2RegisteredClient(clientDto);
        logger.info("********** Finished Seeding OAuth2 Client Data **********");

        logger.info("********** Seeding General Configs Data **********");
//        for (GeneralConfigurationEnum config : GeneralConfigurationEnum.values()) {
//            GeneralConfiguration configuration = generalConfigurationRepository.findByName(config.name()).orElse(null);
//            if (configuration == null) {
//                configuration = new GeneralConfiguration();
//            }
//
//            configuration.setName(config.name());
//            configuration.setValue(config.getValue());
//
//            generalConfigurationRepository.save(configuration);
//        }
        logger.info("********** Finished Seeding General Configs Data **********");

        logger.info("********** Seeding Default SMTP Data **********");
        SMTPConfiguration smtpConfig = smtpConfigurationRepository.findFirstByDefaultConfigTrue().orElse(null);
        if (smtpConfig == null) {
            smtpConfig = new SMTPConfiguration();
        }

        smtpConfig.setHost(smtpHost);
        smtpConfig.setPort(smtpPort);
        smtpConfig.setUsername(smtpUser);
        smtpConfig.setPassword(smtpPasswd);
        smtpConfig.setDefaultConfig(Boolean.TRUE);
        smtpConfig.setAuthRequired(Boolean.parseBoolean(smtpAuthRequired));
        smtpConfig.setStarttlsEnabled(Boolean.parseBoolean(smtpEnableTls));
        smtpConfig.setSenderName("DEMO SYSTEM");
        smtpConfigurationRepository.save(smtpConfig);
        logger.info("********** Finished Seeding Default SMTP Data **********");

        logger.info("********** Seeding Notification Templates Data **********");
//        for (NotificationCategory category : NotificationCategory.values()) {
//            NotificationTemplate notificationTemplate = notificationTemplateRepository.findByNotificationCategory(category).orElse(null);
//            if (notificationTemplate == null) {
//                notificationTemplate = new NotificationTemplate();
//            }
//
//            notificationTemplate.setTitle("NOTIFICATION TITLE");
//            notificationTemplate.setSubject("NOTIFICATION SUBJECT");
//            notificationTemplate.setSalutation("Dear ");
//            notificationTemplate.setMessage("NOTIFICATION MESSAGE");
//            notificationTemplate.setNotificationCategory(category);
//            notificationTemplateRepository.save(notificationTemplate);
//        }
        logger.info("********** Finished Seeding Notification Templates Data **********");
    }

    private List<PermissionDto> getPermissionDtos() {
        List<PermissionDto> permissionDtos = new ArrayList<>();

        permissionDtos.add(new PermissionDto("ROLE_CREATE_USER", "Can Create User", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_EDIT_USER", "Can Edit User", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_VIEW_USER", "Can View User", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_DELETE_USER", "Can Delete User", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));

        permissionDtos.add(new PermissionDto("ROLE_CREATE_ROLE", "Can Create Role", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_EDIT_ROLE", "Can Edit Role", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_VIEW_ROLE", "Can View Role", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_DELETE_ROLE", "Can Delete Role", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));

        permissionDtos.add(new PermissionDto("ROLE_CREATE_OAUTH_CLIENT", "Can Create Oauth2 Client", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_EDIT_OAUTH_CLIENT", "Can Edit Oauth2 Client", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_VIEW_OAUTH_CLIENT", "Can View Oauth2 Client", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_DELETE_OAUTH_CLIENT", "Can Delete Oauth2 Client", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));

        permissionDtos.add(new PermissionDto("ROLE_CREATE_ATTACHMENT", "Can Create Attachment", PermissionGroupEnum.ATTACHMENT_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_EDIT_ATTACHMENT", "Can Edit Attachment", PermissionGroupEnum.ATTACHMENT_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_VIEW_ATTACHMENT", "Can View Attachment", PermissionGroupEnum.ATTACHMENT_PERMISSIONS.getPermissionGroup(), true));
        permissionDtos.add(new PermissionDto("ROLE_DELETE_ATTACHMENT", "Can Delete Attachment", PermissionGroupEnum.ATTACHMENT_PERMISSIONS.getPermissionGroup(), true));

        permissionDtos.add(new PermissionDto("ROLE_VIEW_LOGIN_ATTEMPT", "Can View Login attempts", PermissionGroupEnum.UAA_PERMISSIONS.getPermissionGroup(), true));
        return permissionDtos;
    }

}
