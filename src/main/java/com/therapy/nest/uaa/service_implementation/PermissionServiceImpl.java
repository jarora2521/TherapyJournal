package com.therapy.nest.uaa.service_implementation;

import com.therapy.nest.uaa.entity.PermissionGroup;
import com.therapy.nest.uaa.entity.Role;
import com.therapy.nest.uaa.repository.PermissionGroupRepository;
import com.therapy.nest.uaa.repository.RoleRepository;
import com.therapy.nest.uaa.service.PermissionService;
import com.therapy.nest.shared.utils.Response;
import com.therapy.nest.shared.utils.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionGroupRepository permissionGroupRepository;

    @Override
    public Response<PermissionGroup> getAllPermissions() {
        try {
            logger.info("[PermissionGroup]: Fetching permissions");
            List<PermissionGroup> permissions = permissionGroupRepository.findAll();
            return new Response<>(ResponseCode.SUCCESS, true, "", null, permissions);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to fetch permissions", null);
        }
    }

    @Override
    public Response<PermissionGroup> getPermissionsByRoleUuid(String roleUuid) {
        try {
            logger.info("[PermissionGroup]: Fetching permissions for role: {}", roleUuid);

            if (roleUuid == null)
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Role ID Required", null);

            Role role = roleRepository.findFirstByUuid(roleUuid).orElse(null);
            if (role == null)
                return new Response<>(ResponseCode.NO_RECORD_FOUND, false, "Role not found", null);

            List<PermissionGroup> permissions = permissionGroupRepository.findPermissionGroupsByRoleId(role.getId());

            return new Response<>(ResponseCode.SUCCESS, true, "", null, permissions);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to fetch permissions", null);
        }
    }
}
