package com.therapy.nest.uaa.controllers;

import com.therapy.nest.uaa.dto.Oauth2RegisteredClientDto;
import com.therapy.nest.uaa.entity.Oauth2RegisteredClient;
import com.therapy.nest.uaa.service_implementation.Oauth2RegisteredClientService;
import com.therapy.nest.shared.utils.Response;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@GraphQLApi
public class Oauth2RegisteredClientController {
    @Autowired
    private Oauth2RegisteredClientService oauth2RegisteredClientService;

    @PreAuthorize("hasRole('ROLE_VIEW_OAUTH_CLIENT')")
    @GraphQLQuery(name = "getAllOauth2RegisteredClients", description = "permissions=['ROLE_VIEW_OAUTH_CLIENT']")
    public Response<Oauth2RegisteredClient> getAllOauth2RegisteredClients() {
        return oauth2RegisteredClientService.getAllOauth2RegisteredClients();
    }

    @PreAuthorize("hasAnyRole('ROLE_CREATE_OAUTH_CLIENT', 'ROLE_EDIT_OAUTH_CLIENT')")
    @GraphQLMutation(name = "createOrUpdateOauth2RegisteredClient", description = "permissions=['ROLE_CREATE_OAUTH_CLIENT', 'ROLE_EDIT_OAUTH_CLIENT']")
    public Response<Oauth2RegisteredClient> createOrUpdateOauth2RegisteredClient(@GraphQLArgument(name = "oauth2ClientDto") Oauth2RegisteredClientDto oauth2RegisteredClientDto) {
        return oauth2RegisteredClientService.createOrUpdateOauth2RegisteredClient(oauth2RegisteredClientDto);
    }
}
