package com.therapy.nest.uaa.service;

import com.therapy.nest.uaa.dto.PermissionDto;
import com.therapy.nest.uaa.dto.RoleDto;
import com.therapy.nest.uaa.entity.Role;
import com.therapy.nest.shared.utils.Response;
import com.therapy.nest.shared.utils.pagination.SearchFieldsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    Role seedSuperAdminRole(List<PermissionDto> permissionDtos);

    Page<Role> getAllRolesPageable(List<SearchFieldsDto> searchFields, Pageable pageable);

    Response<Role> getRoleByUuid(String uuid);

    Response<Role> createOrUpdateRole(RoleDto roleDto);

    Response<Role> deleteRole(String uuid);
}
