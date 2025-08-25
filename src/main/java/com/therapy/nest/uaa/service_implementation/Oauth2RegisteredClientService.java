package com.therapy.nest.uaa.service_implementation;

import com.therapy.nest.uaa.dto.Oauth2RegisteredClientDto;
import com.therapy.nest.uaa.entity.Oauth2RegisteredClient;
import com.therapy.nest.uaa.enums.Oauth2ClientAuthMethod;
import com.therapy.nest.uaa.enums.Oauth2TokenFormat;
import com.therapy.nest.uaa.repository.Oauth2RegisteredClientRepository;
import com.therapy.nest.shared.utils.DemoHelper;
import com.therapy.nest.shared.utils.Response;
import com.therapy.nest.shared.utils.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class Oauth2RegisteredClientService implements RegisteredClientRepository {
    private static final Logger logger = LoggerFactory.getLogger(Oauth2RegisteredClientService.class);

    @Autowired
    private Oauth2RegisteredClientRepository oauth2RegisteredClientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void save(RegisteredClient registeredClient) {
    }

    @Override
    public RegisteredClient findById(String id) {
        return mapToClient(oauth2RegisteredClientRepository.findById(id).orElseThrow());
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return mapToClient(oauth2RegisteredClientRepository.findByClientId(clientId).orElseThrow());
    }

    public Response<Oauth2RegisteredClient> getAllOauth2RegisteredClients() {
        try {
            logger.info("[Oauth2RegisteredClient]: Fetching oauth2 registered clients");
            List<Oauth2RegisteredClient> permissions = oauth2RegisteredClientRepository.findAll();
            return new Response<>(ResponseCode.SUCCESS, true, "", null, permissions);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to fetch oauth2 registered clients", null);
        }
    }

    @Transactional
    public Response<Oauth2RegisteredClient> createOrUpdateOauth2RegisteredClient(Oauth2RegisteredClientDto clientDto) {
        try {
            if (clientDto.getClientId() == null || clientDto.getClientId().isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Client ID Required", null);
            if (clientDto.getClientSecret() == null || clientDto.getClientSecret().isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Client Secret Required", null);
            if (clientDto.getClientName() == null || clientDto.getClientName().isEmpty())
                return new Response<>(ResponseCode.NULL_ARGUMENT, false, "Client Name Required", null);

            Oauth2RegisteredClient oauth2Client = new Oauth2RegisteredClient();

            Optional<Oauth2RegisteredClient> existClient = oauth2RegisteredClientRepository.findByClientId(clientDto.getClientId());
            if (existClient.isPresent())
                oauth2Client = existClient.get();

            DemoHelper.copyNonNullProperties(clientDto, oauth2Client);
            oauth2Client.setClientSecret(passwordEncoder.encode(clientDto.getClientSecret()));

            return new Response<>(ResponseCode.SUCCESS, true, "OAuth2 client saved successfully", oauth2RegisteredClientRepository.save(oauth2Client));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to save OAuth2 Client", null);
        }
    }

    private RegisteredClient mapToClient(Oauth2RegisteredClient registeredClient) {
        var tokenFormat = registeredClient.getTokenFormat() == Oauth2TokenFormat.REFERENCE ?
                OAuth2TokenFormat.REFERENCE : OAuth2TokenFormat.SELF_CONTAINED;

        var clientAuthMethod = registeredClient.getClientAuthMethod() == Oauth2ClientAuthMethod.client_secret_basic ?
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC : ClientAuthenticationMethod.CLIENT_SECRET_POST;

        var clientSettings = ClientSettings.builder()
                .requireProofKey(registeredClient.isRequiredProofKey())
                .requireAuthorizationConsent(false)
                .build();

        Set<String> grantTypes = new HashSet<>(Arrays.asList(registeredClient.getGrantTypes().split(",")));
        Set<AuthorizationGrantType> authorizationGrantTypes = grantTypes.stream()
                .map(grantType -> switch (grantType.trim().toLowerCase()) {
                    case "authorization_code" -> AuthorizationGrantType.AUTHORIZATION_CODE;
                    case "client_credentials" -> AuthorizationGrantType.CLIENT_CREDENTIALS;
                    case "refresh_token" -> AuthorizationGrantType.REFRESH_TOKEN;
                    default -> throw new IllegalArgumentException("Unknown grant type: " + grantType);
                })
                .collect(Collectors.toSet());

        Set<String> scopes = new HashSet<>(Arrays.asList(registeredClient.getScopes().split(",")));

        return RegisteredClient.withId(registeredClient.getId())
                .clientId(registeredClient.getClientId())
                .clientSecret(registeredClient.getClientSecret())
                .redirectUri(registeredClient.getRedirectUri())
                .authorizationGrantTypes(grantTypesSet -> grantTypesSet.addAll(authorizationGrantTypes))
                .clientAuthenticationMethod(clientAuthMethod)
                .scopes(scopeSet -> scopeSet.addAll(scopes))
                .clientSettings(clientSettings)
                .tokenSettings(
                        TokenSettings.builder()
                                .accessTokenFormat(tokenFormat)
                                .accessTokenTimeToLive(registeredClient.getAccessTokenValidity())
                                .refreshTokenTimeToLive(registeredClient.getRefreshTokenValidity())
                                .authorizationCodeTimeToLive(registeredClient.getAuthorizationCodeValidity())
                                .build()
                )
                .build();
    }
}
