package com.therapy.nest.shared.config;

import com.therapy.nest.shared.entity.LdapConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class LdapAuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(LdapAuthenticationProvider.class);

    @Value("${ldap.base}")
    private String ldapBaseDn;

    @Value("${ldap.userDn}")
    private String ldapUserDn;

    @Value("${ldap.password}")
    private String ldapPassword;

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.anonymous-read-only}")
    private String ldapAnonymousReadOnly;

    public DefaultSpringSecurityContextSource contextSource(LdapConfiguration ldapConfig) {
        try {
            if (ldapConfig == null) {
                ldapConfig = new LdapConfiguration();
                ldapConfig.setLdapUrl(ldapUrl);
                ldapConfig.setLdapBaseDn(ldapBaseDn);
                ldapConfig.setLdapUserDn(ldapUserDn);
                ldapConfig.setLdapAnonymousReadOnly(Boolean.valueOf(ldapAnonymousReadOnly));
                ldapConfig.setLdapPassword(ldapPassword);
            }

            DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(Collections.singletonList(ldapConfig.getLdapUrl()), ldapConfig.getLdapBaseDn());
            contextSource.afterPropertiesSet();
            contextSource.setAnonymousReadOnly(ldapConfig.getLdapAnonymousReadOnly());
            contextSource.setUserDn(ldapConfig.getLdapUserDn());
            contextSource.setPassword(ldapConfig.getLdapPassword());
            contextSource.setPooled(true);

            return contextSource;
        } catch (Exception e) {
            logger.error("[DefaultSpringSecurityContextSource] ❌: Exception: {}", e.getMessage());
            return null;
        }
    }
}
