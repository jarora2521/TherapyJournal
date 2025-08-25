package com.therapy.nest.uaa.controllers;

import com.therapy.nest.uaa.entity.PermissionGroup;
import com.therapy.nest.uaa.service.PermissionService;
import com.therapy.nest.shared.utils.Response;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@GraphQLApi
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @PreAuthorize("hasRole('ROLE_VIEW_ROLE')")
    @GraphQLQuery(name = "getAllPermissions", description = "permissions=['ROLE_VIEW_ROLE']")
    public Response<PermissionGroup> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @PreAuthorize("hasRole('ROLE_VIEW_ROLE')")
    @GraphQLQuery(name = "getPermissionsByRoleUuid", description = "permissions=['ROLE_VIEW_ROLE']")
    public Response<PermissionGroup> getPermissionsByRoleUuid(@GraphQLArgument(name = "roleUuid") String roleUuid) {
        return permissionService.getPermissionsByRoleUuid(roleUuid);
    }
}
