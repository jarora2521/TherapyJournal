package com.therapy.nest.uaa.service;

import com.therapy.nest.uaa.entity.PermissionGroup;
import com.therapy.nest.shared.utils.Response;

public interface PermissionService {
    Response<PermissionGroup> getAllPermissions();

    Response<PermissionGroup> getPermissionsByRoleUuid(String roleUuid);
}
