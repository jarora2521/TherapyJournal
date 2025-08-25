package com.therapy.nest.uaa.repository;

import com.therapy.nest.uaa.entity.Permission;
import com.therapy.nest.uaa.entity.Role;
import com.therapy.nest.uaa.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long>, JpaSpecificationExecutor<RolePermission> {
    Optional<RolePermission> findFirstByRoleAndPermission(Role role, Permission permission);

    List<RolePermission> findByRole_Uuid(String uuid);

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId")
    List<Permission> findPermissionsByRoleId(Long roleId);
}
