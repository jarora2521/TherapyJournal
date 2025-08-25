package com.therapy.nest.uaa.dto;

import com.therapy.nest.uaa.enums.Oauth2ClientAuthMethod;
import com.therapy.nest.uaa.enums.Oauth2GrantType;
import com.therapy.nest.uaa.enums.Oauth2TokenFormat;
import com.therapy.nest.uaa.enums.TokenDuration;
import lombok.Data;

import java.time.Instant;

@Data
public class Oauth2RegisteredClientDto {
    private String uuid;
    private String clientId;
    private String clientName;
    private String clientSecret;
    private Oauth2GrantType grantType;
    private String redirectUri;
    private String postLogoutRedirectUri;
    private Instant clientSecretExpiresAt;
    private Oauth2ClientAuthMethod clientAuthMethod;
    private TokenDuration accessTokenDuration;
    private Long accessTokenTimeToLive;
    private TokenDuration refreshTokenDuration;
    private Long refreshTokenTimeToLive;
    private TokenDuration authorizationCodeDuration;
    private Long authorizationCodeTimeToLive;
    private String clientSettings;
    private Oauth2TokenFormat tokenFormat;
    private boolean requiredProofKey;
}
