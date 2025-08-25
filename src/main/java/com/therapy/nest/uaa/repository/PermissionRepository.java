package com.therapy.nest.uaa.repository;

import com.therapy.nest.uaa.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    Optional<Permission> findFirstByName(@Param("name") String name);

    Optional<Permission> findFirstByUuid(@Param("permissionUuid") String permissionUuid);
}
