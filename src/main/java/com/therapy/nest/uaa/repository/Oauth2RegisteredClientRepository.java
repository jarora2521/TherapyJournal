package com.therapy.nest.uaa.repository;

import com.therapy.nest.uaa.entity.Oauth2RegisteredClient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Oauth2RegisteredClientRepository extends CrudRepository<Oauth2RegisteredClient, String> {
    Optional<Oauth2RegisteredClient> findByClientId(String clientId);

    List<Oauth2RegisteredClient> findAll();
}
