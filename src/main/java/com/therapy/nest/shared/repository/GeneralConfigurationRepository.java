package com.therapy.nest.shared.repository;

import com.therapy.nest.shared.entity.GeneralConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralConfigurationRepository extends JpaRepository<GeneralConfiguration, Long> {
    Optional<GeneralConfiguration> findByName(String name);

    @Query(value = "SELECT config.value FROM general_configurations config WHERE config.name=?1", nativeQuery = true)
    Optional<String> findConfigValueByName(String name);
}
