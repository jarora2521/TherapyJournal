package com.therapy.nest.shared.config;

import com.therapy.nest.shared.entity.LdapConfiguration;
import com.therapy.nest.uaa.entity.UserAccount;
import com.therapy.nest.uaa.enums.AuthenticationMethod;
import com.therapy.nest.shared.enums.GeneralConfigurationEnum;
import com.therapy.nest.shared.repository.GeneralConfigurationRepository;
import com.therapy.nest.uaa.repository.UserAccountRepository;
import com.therapy.nest.uaa.service.UserAccountService;
import com.therapy.nest.uaa.service_implementation.DatabaseAuthenticationService;
import com.therapy.nest.shared.serviceImpl.LdapAuthenticationService;
import com.therapy.nest.shared.utils.AuthResult;
import com.therapy.nest.shared.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private DatabaseAuthenticationService databaseAuthenticationService;

    @Autowired
    private LdapAuthenticationService ldapAuthenticationService;

    @Autowired
    private GeneralConfigurationRepository generalConfigurationRepository;

    @Autowired
    private UserAccountService userAccountService;

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

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String lockOutTime = generalConfigurationRepository.findConfigValueByName(GeneralConfigurationEnum.LOCKOUT_TIME.toString()).orElse(null);
        if (lockOutTime == null) {
            throw new AccessDeniedException("Account Lockout Time not Configured!");
        }

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        logger.info("[Authentication]: Begin authentication for user: {}", username);

        UserAccount user = userAccountRepository.findByUsername(username).orElse(null);
        if (user == null) {
            logger.error("[Authentication] ❌: User {} not found.", username);
            throw new AccessDeniedException("Incorrect Login Credentials");
        }

        Response<UserAccount> validationResponse = userAccountService.validateLoginUser(user);
        if (!validationResponse.getStatus()) {
            userAccountService.setLoginAttempt(false, false, validationResponse.getMessage(), user);
            throw new AccessDeniedException(validationResponse.getMessage());
        } else {
            user = validationResponse.getData();
        }

        AuthResult<?> result;
        AuthenticationMethod authenticationMethod = user.getAuthenticationMethod();
        LdapConfiguration ldapConfig = new LdapConfiguration();

        if (authenticationMethod != AuthenticationMethod.DATABASE) {
            // TODO: Compute institution LDAP configurations here if is a shared system . . .
            // ldapConfig = account.get().getInstitution().getLdapConfig();
            // BeanUtils.copyProperties(ldapConfig, ldapConfigDto);

            ldapConfig.setLdapUrl(ldapUrl);
            ldapConfig.setLdapBaseDn(ldapBaseDn);
            ldapConfig.setLdapUserDn(ldapUserDn);
            ldapConfig.setLdapAnonymousReadOnly(Boolean.valueOf(ldapAnonymousReadOnly));
            ldapConfig.setLdapPassword(ldapPassword);
        }

        logger.info("[Authentication]: Authentication Method for user {} is {}", username, authenticationMethod);

        if (authenticationMethod == AuthenticationMethod.LDAP) {
            result = ldapAuthenticationService.authenticate(username, password, ldapConfig);
        } else if (authenticationMethod == AuthenticationMethod.DATABASE) {
            result = databaseAuthenticationService.authenticate(username, password);
        } else if (authenticationMethod == AuthenticationMethod.BOTH) {
            result = ldapAuthenticationService.authenticate(username, password, ldapConfig);
            if (result.isError()) {
                result = databaseAuthenticationService.authenticate(username, password);
            }
        } else {
            logger.error("[Authentication] ❌: Authentication Method for user {} is not supported.", username);
            result = new AuthResult<>(null, "Authentication Method not Supported", true, new ArrayList<>());
        }

        if (result != null && result.isError()) {
            logger.error("[Authentication] ❌: Incorrect Login credentials.");

            int attemptCount = user.getAttemptCount();
            int maxLoginAttempts = 0;
            boolean lockoutAccount = false;

            if (user.isAccountNonLocked()) {
                String configLoginAttempts = generalConfigurationRepository.findConfigValueByName(GeneralConfigurationEnum.MAX_LOGIN_ATTEMPTS.toString()).orElse(null);
                if (configLoginAttempts != null) {
                    maxLoginAttempts = Integer.parseInt(configLoginAttempts);
                }

                if (attemptCount == maxLoginAttempts) {
                    lockoutAccount = true;
                    userAccountService.lockUserAccount(user.getId());
                    result.setMessage(String.format("Too many login attempts. Account is locked for %s minutes.", lockOutTime));
                } else {
                    logger.info("[Authentication]: Incrementing login attempts for user {} to {}", username, attemptCount + 1);
                }
            }

            userAccountService.incrementLoginAttemptCount(user.getId());
            userAccountService.setLoginAttempt(false, lockoutAccount, result.getMessage(), user);

            throw new AccessDeniedException(result.getMessage());
        }

        userAccountService.setLoginAttempt(true, false, "", user);

        return new UsernamePasswordAuthenticationToken(result.getData(), password, result.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
