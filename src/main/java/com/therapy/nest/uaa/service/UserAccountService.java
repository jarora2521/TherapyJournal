package com.therapy.nest.uaa.service;

import com.therapy.nest.uaa.dto.UserDto;
import com.therapy.nest.uaa.entity.LoginAttempt;
import com.therapy.nest.uaa.entity.Role;
import com.therapy.nest.uaa.entity.UserAccount;
import com.therapy.nest.shared.utils.Response;
import com.therapy.nest.shared.utils.pagination.SearchFieldsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserAccountService {
    void createSuperAdminAccount(UserDto userDto, Role role);

    Page<UserAccount> getAllUsersPageable(List<SearchFieldsDto> searchFields, Pageable pageable);

    Response<UserAccount> getUserByUuid(String uuid);

    Response<UserAccount> createOrUpdateUser(UserDto userDto);

    Response<UserAccount> activateOrDeactivateUser(String uuid);

    Response<UserAccount> validateLoginUser(UserAccount account);

    void setLoginAttempt(Boolean success, Boolean lockoutAccount, String errorMessage, UserAccount user);

    String getClientIp();

    String getUserAgent();

    void lockUserAccount(Long id);

    void unLockUserAccount(Long id);

    void incrementLoginAttemptCount(Long id);

    Page<LoginAttempt> getAllLoginAttemptsPageable(List<SearchFieldsDto> searchFields, Pageable pageable);

    Response<UserAccount> unlockUserAccount(String uuid);
}
