package com.therapy.nest.uaa.service_implementation;

import com.therapy.nest.uaa.dto.PermissionDto;
import com.therapy.nest.uaa.dto.RoleDto;
import com.therapy.nest.uaa.entity.Permission;
import com.therapy.nest.uaa.entity.PermissionGroup;
import com.therapy.nest.uaa.entity.Role;
import com.therapy.nest.uaa.entity.RolePermission;
import com.therapy.nest.uaa.repository.PermissionGroupRepository;
import com.therapy.nest.uaa.repository.PermissionRepository;
import com.therapy.nest.uaa.repository.RolePermissionRepository;
import com.therapy.nest.uaa.repository.RoleRepository;
import com.therapy.nest.uaa.service.RoleService;
import com.therapy.nest.shared.utils.Response;
import com.therapy.nest.shared.utils.ResponseCode;
import com.therapy.nest.shared.utils.pagination.GenericSpecificationSearch;
import com.therapy.nest.shared.utils.pagination.SearchFieldsDto;
import com.therapy.nest.shared.utils.pagination.SearchOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private PermissionGroupRepository permissionGroupRepository;

    @Transactional
    @Override
    public Role seedSuperAdminRole(List<PermissionDto> permissionDtos) {
        try {
            String roleName = "SUPER_ADMIN";
            Optional<Role> adminRole = roleRepository.findFirstByIsGlobalTrue();

            Role role = new Role();

            if (adminRole.isEmpty()) {
                role.setName(roleName);
                role.setIsGlobal(true);
                role = roleRepository.save(role);

                logger.info("Super Admin role created. Assigning Permissions . . . . !");
            } else {
                role = adminRole.get();
                logger.info("Super Admin role found. Assigning Permissions . . . . !");
            }

            List<Permission> permissions = new ArrayList<>();

            if (!permissionDtos.isEmpty()) {
                List<Permission> permissionList = new ArrayList<>();
                PermissionGroup permissionGroup;

                for (PermissionDto permissionDto : permissionDtos) {
                    permissionGroup = permissionGroupRepository.findFirstByName(permissionDto.getPermissionGroup()).orElse(null);
                    if (permissionGroup == null) {
                        permissionGroup = permissionGroupRepository.save(new PermissionGroup(permissionDto.getPermissionGroup()));
                    }

                    Permission existPermission = permissionRepository.findFirstByName(permissionDto.getName()).orElse(null);
                    if (existPermission == null) {
                        permissionList.add(new Permission(permissionDto.getName(), permissionDto.getDisplayName(), permissionDto.isGlobal(), permissionGroup));
                    } else {
                        existPermission.setDisplayName(permissionDto.getDisplayName());
                        existPermission.setGlobal(permissionDto.isGlobal());
                        existPermission.setPermissionGroup(permissionGroup);
                        permissionList.add(existPermission);
                    }
                }

                permissions = permissionRepository.saveAll(permissionList);
            }

            List<RolePermission> rolePermissionList = new ArrayList<>();

            for (Permission permission : permissions) {
                RolePermission existPermission = rolePermissionRepository.findFirstByRoleAndPermission(role, permission).orElse(null);
                if (existPermission == null) {
                    rolePermissionList.add(new RolePermission(permission, role));
                }
            }

            rolePermissionRepository.saveAll(rolePermissionList);

            return role;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Page<Role> getAllRolesPageable(List<SearchFieldsDto> searchFields, Pageable pageable) {
        try {
            logger.info("[Role]: Get all Roles Pageable");

            GenericSpecificationSearch<Role> genericSpec = new GenericSpecificationSearch<>();
            Specification<Role> queryParams = Specification.where(genericSpec.createSpecification(new SearchFieldsDto("deleted", false, SearchOperationType.Equals)));

            if (searchFields != null && !searchFields.isEmpty()) {
                Specification<Role> specification1 = genericSpec.getSearchSpec(searchFields);
                queryParams = queryParams.and(specification1);
            }

            return roleRepository.findAll(queryParams, pageable);
        } catch (Exception e) {
            e.printStackTrace();
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    public Response<Role> getRoleByUuid(String uuid) {
        try {
            if (uuid == null || uuid.isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Role ID required", null);

            Role role = roleRepository.findFirstByUuid(uuid).orElse(null);
            if (role == null)
                return new Response<>(ResponseCode.NO_RECORD_FOUND, false, String.format("Role: %s DOES NOT EXIST", uuid), null);

            return new Response<>(ResponseCode.SUCCESS, true, "", role);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to get Role", null);
        }
    }

    @Override
    @Transactional
    public Response<Role> createOrUpdateRole(RoleDto roleDto) {
        try {
            if (roleDto.getName() == null || roleDto.getName().isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Role Name Required", null);

            Role role;
            boolean updated = roleDto.getUuid() != null;

            if (updated) {
                role = roleRepository.findFirstByUuid(roleDto.getUuid()).orElse(null);
                if (role == null) return new Response<>(ResponseCode.NO_RECORD_FOUND, false, "Role not found", null);
            } else {
                if (roleRepository.findFirstByName(roleDto.getName()).isPresent())
                    return new Response<>(ResponseCode.DUPLICATE, false, "Role has already been registered", null);

                role = new Role();
            }

            role.setName(roleDto.getName());
            Role saved = roleRepository.save(role);

            if (roleDto.getPermissions() != null && !roleDto.getPermissions().isEmpty()) {
                saved.setPermissions(updateRolePermissions(saved, roleDto.getPermissions()));
            }

            return new Response<>(ResponseCode.SUCCESS, true, updated ? "Role updated successfully" : "Role created successfully", saved);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to save role", null);
        }
    }

    private List<RolePermission> updateRolePermissions(Role role, List<String> permissionUuids) {
        rolePermissionRepository.deleteAll(rolePermissionRepository.findByRole_Uuid(role.getUuid()));

        List<RolePermission> rolePermissions = new ArrayList<>();
        for (String permissionUuid : permissionUuids) {
            Permission permission = permissionRepository.findFirstByUuid(permissionUuid).orElse(null);
            if (permission != null) {
                RolePermission rolePermission = new RolePermission(permission, role);
                rolePermissions.add(rolePermission);
            }
        }

        return rolePermissionRepository.saveAll(rolePermissions);
    }

    @Override
    @Transactional
    public Response<Role> deleteRole(String uuid) {
        try {
            if (uuid == null || uuid.isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Role ID Required", null);

            Role role = roleRepository.findFirstByUuid(uuid).orElse(null);
            if (role == null)
                return new Response<>(ResponseCode.NO_RECORD_FOUND, false, "Role not found", null);
            else if (!role.getUsers().isEmpty())
                return new Response<>(ResponseCode.DATA_IN_USE, false, "Role has been assigned to other user(s)", null);

            roleRepository.delete(role);

            return new Response<>(ResponseCode.SUCCESS, true, "Role deleted successfully", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to delete Role", null);
        }
    }
}
