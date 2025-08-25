package com.therapy.nest.uaa.repository;

import com.therapy.nest.uaa.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long>, JpaSpecificationExecutor<LoginAttempt> {
}
