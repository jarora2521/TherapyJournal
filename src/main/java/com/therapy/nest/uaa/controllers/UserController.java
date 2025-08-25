package com.therapy.nest.uaa.controllers;

import com.therapy.nest.uaa.dto.UserDto;
import com.therapy.nest.uaa.entity.LoginAttempt;
import com.therapy.nest.uaa.entity.UserAccount;
import com.therapy.nest.uaa.service.UserAccountService;
import com.therapy.nest.shared.utils.Response;
import com.therapy.nest.shared.utils.pagination.PageableConfig;
import com.therapy.nest.shared.utils.pagination.PageableParam;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Service
@GraphQLApi
@RestController
public class UserController {
    @Autowired
    private PageableConfig pageableConfig;

    @Autowired
    private UserAccountService userAccountService;

    @PreAuthorize("hasAuthority('ROLE_VIEW_USER')")
    @GraphQLQuery(name = "getAllUsersPageable", description = "ROLE_VIEW_USER")
    public Page<UserAccount> getAllUsersPageable(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam) {
        Pageable pageable = pageableConfig.pageable(pageableParam);
        if (pageableParam != null && pageableParam.getSearchFields() != null) {
            return userAccountService.getAllUsersPageable(pageableParam.getSearchFields(), pageable);
        }
        return userAccountService.getAllUsersPageable(new ArrayList<>(), pageable);
    }

    @PreAuthorize("hasAuthority('ROLE_VIEW_USER')")
    @GraphQLQuery(name = "getUserByUuid", description = "permissions=['ROLE_VIEW_USER']")
    public Response<UserAccount> getUserByUuid(@GraphQLArgument(name = "uuid") String uuid) {
        return userAccountService.getUserByUuid(uuid);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CREATE_USER', 'ROLE_EDIT_USER')")
    @GraphQLMutation(name = "createOrUpdateUser", description = "permissions=['ROLE_CREATE_USER', 'ROLE_EDIT_USER']")
    public Response<UserAccount> createOrUpdateUser(@GraphQLArgument(name = "userDto") UserDto userDto) {
        return userAccountService.createOrUpdateUser(userDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ACTIVATE_OR_DEACTIVATE_USER')")
    @GraphQLMutation(name = "activateOrDeactivateUser", description = "permissions=['ROLE_ACTIVATE_OR_DEACTIVATE_USER']")
    public Response<UserAccount> activateOrDeactivateUser(@GraphQLArgument(name = "uuid") String uuid) {
        return userAccountService.activateOrDeactivateUser(uuid);
    }

    @PreAuthorize("hasAuthority('ROLE_VIEW_LOGIN_ATTEMPT')")
    @GraphQLQuery(name = "getAllLoginAttemptsPageable", description = "ROLE_VIEW_LOGIN_ATTEMPT")
    public Page<LoginAttempt> getAllLoginAttemptsPageable(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam) {
        Pageable pageable = pageableConfig.pageable(pageableParam);
        if (pageableParam != null && pageableParam.getSearchFields() != null) {
            return userAccountService.getAllLoginAttemptsPageable(pageableParam.getSearchFields(), pageable);
        }
        return userAccountService.getAllLoginAttemptsPageable(new ArrayList<>(), pageable);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CREATE_USER', 'ROLE_EDIT_USER')")
    @GraphQLMutation(name = "unlockUserAccount", description = "permissions=['ROLE_CREATE_USER', 'ROLE_EDIT_USER']")
    public Response<UserAccount> unlockUserAccount(@GraphQLArgument(name = "uuid") String uuid) {
        return userAccountService.unlockUserAccount(uuid);
    }

}
