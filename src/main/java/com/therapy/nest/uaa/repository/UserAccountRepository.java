package com.therapy.nest.uaa.repository;

import com.therapy.nest.uaa.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long>, JpaSpecificationExecutor<UserAccount> {
    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findFirstByUuid(String uuid);

    @Modifying
    @Query(value = "UPDATE users SET account_non_locked = false, attempt_count = attempt_count + 1,  lock_time = ?2 WHERE id = ?1", nativeQuery = true)
    void lockUserAccount(Long id, LocalDateTime now);

    @Modifying
    @Query(value = "UPDATE users SET account_non_locked = true, attempt_count = 0,  lock_time = null WHERE id = ?1", nativeQuery = true)
    void unLockUserAccount(Long id);

    @Modifying
    @Query(value = "UPDATE users SET attempt_count = attempt_count + 1 WHERE id = :id", nativeQuery = true)
    void incrementLoginAttemptCount(@Param("id") Long id);
}
