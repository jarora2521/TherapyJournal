package com.therapy.nest.uaa.repository;

import com.therapy.nest.uaa.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Optional<Role> findFirstByIsGlobalTrue();

    Optional<Role> findFirstByUuid(String uuid);

    Optional<Role> findFirstByName(String name);
}
