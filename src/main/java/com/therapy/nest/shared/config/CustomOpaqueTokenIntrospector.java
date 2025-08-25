package com.therapy.nest.shared.config;

import com.therapy.nest.uaa.service_implementation.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    private static final Logger logger = LoggerFactory.getLogger(CustomOpaqueTokenIntrospector.class);

    @Value("${oauth2.introspect-uri}")
    private String introspectUri;

    @Value("${oauth2.client-id}")
    private String oauth2ClientId;

    @Value("${oauth2.client-secret}")
    private String oauth2ClientSecret;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        try {
            OpaqueTokenIntrospector delegate = new NimbusOpaqueTokenIntrospector(introspectUri, oauth2ClientId, oauth2ClientSecret);
            OAuth2AuthenticatedPrincipal principal = delegate.introspect(token);

            Map<String, Object> attributes = userDetailsService.getAttributes(principal.getName());
            Collection<GrantedAuthority> grantedAuthorities = userDetailsService.getAuthoritiesByPrincipalName(principal.getName());

            return new DefaultOAuth2AuthenticatedPrincipal(principal.getName(), attributes, grantedAuthorities);
        } catch (Exception e) {
            logger.error("[OAuth2AuthenticatedPrincipal] ❌: Failed to introspect token: {}. Token isn't active", token);

            Map<String, Object> defaultAttributes = new HashMap<>();
            defaultAttributes.put("error", "Invalid token");

            return new DefaultOAuth2AuthenticatedPrincipal("anonymous", defaultAttributes, Collections.emptyList());
        }
    }
}
