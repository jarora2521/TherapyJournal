package com.therapy.nest.shared.repository;

import com.therapy.nest.shared.entity.SMTPConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SMTPConfigurationRepository extends JpaRepository<SMTPConfiguration, Long> {
    Optional<SMTPConfiguration> findFirstByUuid(String uuid);

    Optional<SMTPConfiguration> findFirstByDefaultConfigTrue();
}
