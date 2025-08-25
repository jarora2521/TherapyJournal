package com.therapy.nest.uaa.service_implementation;

import com.therapy.nest.shared.config.RabbitMQProducer;
import com.therapy.nest.uaa.dto.UserDto;
import com.therapy.nest.uaa.entity.LoginAttempt;
import com.therapy.nest.uaa.entity.Role;
import com.therapy.nest.uaa.entity.UserAccount;
import com.therapy.nest.uaa.enums.AuthenticationMethod;
import com.therapy.nest.shared.enums.GeneralConfigurationEnum;
import com.therapy.nest.shared.enums.NotificationCategory;
import com.therapy.nest.shared.enums.NotificationType;
import com.therapy.nest.shared.repository.GeneralConfigurationRepository;
import com.therapy.nest.uaa.repository.LoginAttemptRepository;
import com.therapy.nest.uaa.repository.RoleRepository;
import com.therapy.nest.uaa.repository.UserAccountRepository;
import com.therapy.nest.uaa.service.UserAccountService;
import com.therapy.nest.shared.utils.DemoHelper;
import com.therapy.nest.shared.utils.Response;
import com.therapy.nest.shared.utils.ResponseCode;
import com.therapy.nest.shared.utils.pagination.GenericSpecificationSearch;
import com.therapy.nest.shared.utils.pagination.SearchFieldsDto;
import com.therapy.nest.shared.utils.pagination.SearchOperationType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
    private static final Logger logger = LoggerFactory.getLogger(UserAccountServiceImpl.class);

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final GeneralConfigurationRepository generalConfigurationRepository;
    private final RabbitMQProducer rabbitMQProducer;


    @Override
    @Transactional
    public void createSuperAdminAccount(UserDto userDto, Role role) {
        try {
            UserAccount userAccount = new UserAccount();

            Optional<UserAccount> existUserAccount = userAccountRepository.findByUsername(userDto.getEmail());
            if (existUserAccount.isPresent())
                userAccount = existUserAccount.get();

            DemoHelper.copyNonNullProperties(userDto, userAccount);
            userAccount.setUsername(userDto.getEmail());
            userAccount.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userAccount.setRole(role);
            userAccount.setIsSuperAdmin(true);

            userAccountRepository.save(userAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Page<UserAccount> getAllUsersPageable(List<SearchFieldsDto> searchFields, Pageable pageable) {
        try {
            logger.info("[UserAccount]: Get all User Accounts Pageable");

            GenericSpecificationSearch<UserAccount> genericSpec = new GenericSpecificationSearch<>();
            Specification<UserAccount> queryParams = Specification.where(genericSpec.createSpecification(new SearchFieldsDto("deleted", false, SearchOperationType.Equals)));

            if (searchFields != null && !searchFields.isEmpty()) {
                Specification<UserAccount> specification1 = genericSpec.getSearchSpec(searchFields);
                queryParams = queryParams.and(specification1);
            }

            return userAccountRepository.findAll(queryParams, pageable);
        } catch (Exception e) {
            e.printStackTrace();
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    public Response<UserAccount> getUserByUuid(String uuid) {
        try {
            if (uuid == null || uuid.isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "User ID required", null);

            UserAccount userAccount = userAccountRepository.findFirstByUuid(uuid).orElse(null);
            if (userAccount == null)
                return new Response<>(ResponseCode.NO_RECORD_FOUND, false, String.format("User: %s DOES NOT EXIST", uuid), null);

            return new Response<>(ResponseCode.SUCCESS, true, "", userAccount);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to get User", null);
        }
    }

    @Override
    @Transactional
    public Response<UserAccount> createOrUpdateUser(UserDto userDto) {
        try {
            boolean updated = userDto.getUuid() != null;

            if (userDto.getFirstName() == null || userDto.getFirstName().isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "First Name Required", null);
            if (userDto.getLastName() == null || userDto.getLastName().isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Last Name Required", null);
            if (userDto.getEmail() == null || userDto.getEmail().isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Email Required", null);
            if (userDto.getAuthenticationMethod() == null)
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Authentication Method Required", null);
            if (
                    (userDto.getAuthenticationMethod() == AuthenticationMethod.DATABASE || userDto.getAuthenticationMethod() == AuthenticationMethod.BOTH) &&
                            (!updated && (userDto.getPassword() == null || userDto.getPassword().isEmpty()))
            )
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Password Required", null);

            logger.info("[UserAccount]: {}", updated ? "Updating user: " + userDto.getEmail() : "Creating user: " + userDto.getEmail());

            Role role = null;
            UserAccount userAccount;

            if (userDto.getRoleUuid() != null && !userDto.getRoleUuid().isEmpty()) {
                role = roleRepository.findFirstByUuid(userDto.getUuid()).orElse(null);
                if (role == null)
                    return new Response<>(ResponseCode.NO_RECORD_FOUND, false, "Role not found", null);
            }

            if (updated) {
                userAccount = userAccountRepository.findFirstByUuid(userDto.getUuid()).orElse(null);
                if (userAccount == null)
                    return new Response<>(ResponseCode.NO_RECORD_FOUND, false, "User not found", null);
            } else {
                if (userAccountRepository.findByUsername(userDto.getEmail()).isPresent())
                    return new Response<>(ResponseCode.DUPLICATE, false, "User has already been registered", null);

                userAccount = new UserAccount();
            }

            userAccount.setEmail(userDto.getEmail());
            userAccount.setFirstName(userDto.getFirstName());
            userAccount.setMiddleName(userDto.getMiddleName());
            userAccount.setLastName(userDto.getLastName());
            userAccount.setPhone(userDto.getPhone());
            userAccount.setAuthenticationMethod(userDto.getAuthenticationMethod());
            userAccount.setPicture(userDto.getPicture());
            userAccount.setEmail(userDto.getEmail());
            userAccount.setUsername(userDto.getEmail());
            userAccount.setRole(role);

            if (userDto.getAuthenticationMethod() == AuthenticationMethod.LDAP) {
                userAccount.setPassword(passwordEncoder.encode(System.currentTimeMillis() + userAccount.getUuid()));
                System.out.println(System.currentTimeMillis() + userAccount.getUuid());
            } else {
                userAccount.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            UserAccount saved = userAccountRepository.save(userAccount);

            if (!updated) {
                rabbitMQProducer.sendMessage(saved.getId(), NotificationType.EMAIL, NotificationCategory.InvitationNotification, new ArrayList<>());
            }

            return new Response<>(ResponseCode.SUCCESS, true, updated ? "User updated successfully" : "User created successfully", saved);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to save user", null);
        }
    }

    @Override
    @Transactional
    public Response<UserAccount> activateOrDeactivateUser(String uuid) {
        try {
            if (uuid == null || uuid.isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "User ID Required", null);

            UserAccount userAccount = userAccountRepository.findFirstByUuid(uuid).orElse(null);
            if (userAccount == null)
                return new Response<>(ResponseCode.NO_RECORD_FOUND, false, "User not found", null);
            else if (userAccount.getIsSuperAdmin())
                return new Response<>(ResponseCode.UNAUTHORIZED, false, "Cannot modify Admin User", null);

            userAccount.setEnabled(!userAccount.isEnabled());
            UserAccount saved = userAccountRepository.save(userAccount);

            return new Response<>(ResponseCode.SUCCESS, true, "User saved successfully", saved);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to save User", null);
        }
    }

    @Override
    public Response<UserAccount> validateLoginUser(UserAccount user) {
        String message = "";
        int code = ResponseCode.UNAUTHORIZED;
        boolean status = true;

        if (!user.isAccountNonExpired()) {
            message = "Account Expired!";
            status = false;
        }

        if (!user.isCredentialsNonExpired()) {
            message = "Credentials Expired";
            status = false;
        }

        if (!user.isEnabled()) {
            message = "Account Disabled!";
            status = false;
        }

        if (!user.isAccountNonLocked()) {
            String lockOutTime = generalConfigurationRepository.findConfigValueByName(GeneralConfigurationEnum.LOCKOUT_TIME.toString()).orElse(null);
            if (lockOutTime == null) {
                message = "Account Lockout Time not Configured!";
                return new Response<>(ResponseCode.UNAUTHORIZED, false, message, null);
            }

            long lockDuration = Long.parseLong(lockOutTime);
            LocalDateTime lockTime = user.getLockTime();
            LocalDateTime lockUntil = lockTime.plusMinutes(lockDuration);

            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(lockUntil)) {
                code = ResponseCode.SUCCESS;

                unLockUserAccount(user.getId());
                user.setAccountNonLocked(true);
            } else {
                Duration timeRemaining = Duration.between(lockUntil, now);
                if (timeRemaining.toMinutes() > 0) {
                    message = String.format("Too many login attempts. Account is locked for %s minutes.", timeRemaining.toMinutes());
                } else {
                    message = String.format("Too many login attempts. Account is locked for %s seconds.", Math.abs(timeRemaining.toSeconds()));
                }

                status = false;
                setLoginAttempt(false, false, message, user);
            }
        }

        logger.error("[ValidateLoginUser] {}: {}", status ? "✅" : "❌", message);

        return new Response<>(code, status, message, user);
    }

    @Override
    @Transactional
    public void setLoginAttempt(Boolean success, Boolean lockoutAccount, String errorMessage, UserAccount user) {
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setUser(user);
        loginAttempt.setAttemptTime(LocalDateTime.now());
        loginAttempt.setIpAddress(getClientIp());
        loginAttempt.setUserAgent(getUserAgent());
        loginAttempt.setSuccessful(success);
        loginAttempt.setLockoutTriggered(lockoutAccount);
        loginAttempt.setFailureReason(errorMessage);
        loginAttemptRepository.save(loginAttempt);
    }

    @Override
    public String getClientIp() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        }
        return "unknown";
    }

    @Override
    public String getUserAgent() {
        HttpServletRequest request = getCurrentHttpRequest();
        return (request != null) ? request.getHeader("User-Agent") : "UNKNOWN";
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attributes != null) ? attributes.getRequest() : null;
    }

    @Override
    @Transactional
    public void lockUserAccount(Long id) {
        userAccountRepository.lockUserAccount(id, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void unLockUserAccount(Long id) {
        userAccountRepository.unLockUserAccount(id);
    }

    @Override
    @Transactional
    public void incrementLoginAttemptCount(Long id) {
        userAccountRepository.incrementLoginAttemptCount(id);
    }

    @Override
    public Page<LoginAttempt> getAllLoginAttemptsPageable(List<SearchFieldsDto> searchFields, Pageable pageable) {
        try {
            logger.info("[LoginAttempt]: Get all Login attempts Pageable");

            GenericSpecificationSearch<LoginAttempt> genericSpec = new GenericSpecificationSearch<>();
            Specification<LoginAttempt> queryParams = Specification.where(genericSpec.createSpecification(new SearchFieldsDto("deleted", false, SearchOperationType.Equals)));

            if (searchFields != null && !searchFields.isEmpty()) {
                Specification<LoginAttempt> specification1 = genericSpec.getSearchSpec(searchFields);
                queryParams = queryParams.and(specification1);
            }

            return loginAttemptRepository.findAll(queryParams, pageable);
        } catch (Exception e) {
            e.printStackTrace();
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    @Transactional
    public Response<UserAccount> unlockUserAccount(String uuid) {
        try {
            if (uuid == null || uuid.isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "User ID Required", null);

            UserAccount userAccount = userAccountRepository.findFirstByUuid(uuid).orElse(null);
            if (userAccount == null)
                return new Response<>(ResponseCode.NO_RECORD_FOUND, false, "User not found", null);
            else if (userAccount.isAccountNonLocked())
                return new Response<>(ResponseCode.NO_DATA_CHANGED, false, "User account has not been locked", null);

            userAccount.setAccountNonLocked(true);
            userAccount.setLockTime(null);
            userAccount.setAttemptCount(0);
            UserAccount saved = userAccountRepository.save(userAccount);

            return new Response<>(ResponseCode.SUCCESS, true, "User Account unlocked successfully", saved);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to unlock User account", null);
        }
    }
}
