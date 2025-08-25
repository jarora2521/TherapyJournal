package com.therapy.nest.uaa.controllers;

import com.therapy.nest.uaa.dto.RoleDto;
import com.therapy.nest.uaa.entity.Role;
import com.therapy.nest.uaa.service.RoleService;
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

import java.util.ArrayList;

@Service
@GraphQLApi
public class RoleController {
    @Autowired
    private PageableConfig pageableConfig;

    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasRole('ROLE_VIEW_ROLE')")
    @GraphQLQuery(name = "getAllRolesPageable", description = "ROLE_VIEW_ROLE")
    public Page<Role> getAllRolesPageable(@GraphQLArgument(name = "pageableParam") PageableParam pageableParam) {
        Pageable pageable = pageableConfig.pageable(pageableParam);
        if (pageableParam != null && pageableParam.getSearchFields() != null) {
            return roleService.getAllRolesPageable(pageableParam.getSearchFields(), pageable);
        }
        return roleService.getAllRolesPageable(new ArrayList<>(), pageable);
    }

    @PreAuthorize("hasRole('ROLE_VIEW_ROLE')")
    @GraphQLQuery(name = "getRoleByUuid", description = "permissions=['ROLE_VIEW_ROLE']")
    public Response<Role> getRoleByUuid(@GraphQLArgument(name = "uuid") String uuid) {
        return roleService.getRoleByUuid(uuid);
    }

    @PreAuthorize("hasAnyRole('ROLE_CREATE_ROLE', 'ROLE_EDIT_ROLE')")
    @GraphQLMutation(name = "createOrUpdateRole", description = "permissions=['ROLE_CREATE_ROLE', 'ROLE_EDIT_ROLE']")
    public Response<Role> createOrUpdateRole(@GraphQLArgument(name = "roleDto") RoleDto roleDto) {
        return roleService.createOrUpdateRole(roleDto);
    }

    @PreAuthorize("hasRole('ROLE_DELETE_ROLE')")
    @GraphQLMutation(name = "deleteRole", description = "permissions=['ROLE_DELETE_ROLE']")
    public Response<Role> deleteRole(@GraphQLArgument(name = "uuid") String uuid) {
        return roleService.deleteRole(uuid);
    }
}
