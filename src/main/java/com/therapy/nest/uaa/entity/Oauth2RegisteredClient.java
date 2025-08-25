package com.therapy.nest.uaa.entity;

import com.therapy.nest.uaa.enums.Oauth2ClientAuthMethod;
import com.therapy.nest.uaa.enums.Oauth2TokenFormat;
import com.therapy.nest.uaa.enums.TokenDuration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.leangen.graphql.annotations.GraphQLIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "oauth2_registered_clients")
public class Oauth2RegisteredClient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, insertable = false, updatable = false)
    private String id;

    @Column(name = "client_id", unique = true)
    private String clientId;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_secret", nullable = false)
    @JsonIgnore
    @GraphQLIgnore
    private String clientSecret;

    @Column(name = "scopes", columnDefinition = "VARCHAR(255) DEFAULT 'read,write,openid'")
    private String scopes = "read,write,openid";

    @Column(name = "grant_types", columnDefinition = "VARCHAR(255) DEFAULT 'authorization_code,refresh_token'")
    private String grantTypes = "authorization_code,refresh_token";

    @Column(name = "redirect_uri")
    private String redirectUri;

    @Column(name = "post_logout_redirect_uris")
    private String postLogoutRedirectUri;

    @Column(name = "issued_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Column(name = "resource_ids")
    private String resourceIds;

    @Column(name = "client_secret_expires_at")
    private Instant clientSecretExpiresAt;

    @Column(name = "client_authentication_methods")
    @Enumerated(value = EnumType.STRING)
    private Oauth2ClientAuthMethod clientAuthMethod = Oauth2ClientAuthMethod.client_secret_basic;

    @Column(name = "access_token_duration")
    @Enumerated(value = EnumType.STRING)
    private TokenDuration accessTokenDuration = TokenDuration.HOURS;

    @Column(name = "access_token_time_to_live")
    private Long accessTokenTimeToLive = 1L;

    @Column(name = "refresh_token_duration")
    @Enumerated(value = EnumType.STRING)
    private TokenDuration refreshTokenDuration = TokenDuration.MINUTES;

    @Column(name = "refresh_token_time_to_live")
    private Long refreshTokenTimeToLive = 30L;

    @Column(name = "authorization_code_duration")
    @Enumerated(value = EnumType.STRING)
    private TokenDuration authorizationCodeDuration = TokenDuration.MINUTES;

    @Column(name = "authorization_code_time_to_live")
    private Long authorizationCodeTimeToLive = 5L;

    @Column(name = "client_settings")
    private String clientSettings;

    @Column(name = "token_settings")
    @Enumerated(value = EnumType.STRING)
    private Oauth2TokenFormat tokenFormat = Oauth2TokenFormat.REFERENCE;

    @Column(name = "required_proof_key", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean requiredProofKey = false;

    public Duration getAccessTokenValidity() {
        if (accessTokenDuration == null || accessTokenTimeToLive == null) return Duration.ZERO;

        return switch (accessTokenDuration) {
            case MINUTES -> Duration.ofMinutes(accessTokenTimeToLive);
            case HOURS -> Duration.ofHours(accessTokenTimeToLive);
            case DAYS -> Duration.ofDays(accessTokenTimeToLive);
        };
    }

    public Duration getRefreshTokenValidity() {
        if (refreshTokenDuration == null || refreshTokenTimeToLive == null) return Duration.ZERO;

        return switch (refreshTokenDuration) {
            case MINUTES -> Duration.ofMinutes(refreshTokenTimeToLive);
            case HOURS -> Duration.ofHours(refreshTokenTimeToLive);
            case DAYS -> Duration.ofDays(refreshTokenTimeToLive);
        };
    }

    public Duration getAuthorizationCodeValidity() {
        if (authorizationCodeDuration == null || authorizationCodeTimeToLive == null) return Duration.ZERO;

        return switch (authorizationCodeDuration) {
            case MINUTES -> Duration.ofMinutes(authorizationCodeTimeToLive);
            case HOURS -> Duration.ofHours(authorizationCodeTimeToLive);
            case DAYS -> Duration.ofDays(authorizationCodeTimeToLive);
        };
    }
}
