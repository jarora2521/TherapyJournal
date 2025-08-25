package com.therapy.nest.uaa.repository;

import com.therapy.nest.uaa.entity.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Long>, JpaSpecificationExecutor<PermissionGroup> {
    Optional<PermissionGroup> findFirstByName(String name);

    @Query("SELECT DISTINCT pg FROM PermissionGroup pg JOIN pg.permissions p JOIN RolePermission rp ON rp.permission = p WHERE rp.role.id = :roleId")
    List<PermissionGroup> findPermissionGroupsByRoleId(@Param("roleId") Long roleId);
}
