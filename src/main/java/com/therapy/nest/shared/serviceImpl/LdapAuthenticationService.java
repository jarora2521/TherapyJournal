package com.therapy.nest.shared.serviceImpl;

import com.therapy.nest.shared.config.LdapAuthenticationProvider;
import com.therapy.nest.shared.entity.LdapConfiguration;
import com.therapy.nest.uaa.entity.UserAccount;
import com.therapy.nest.uaa.repository.UserAccountRepository;
import com.therapy.nest.uaa.service_implementation.UserDetailsServiceImpl;
import com.therapy.nest.shared.utils.AuthResult;
import graphql.com.google.common.collect.ImmutableMap;
import org.hibernate.NonUniqueResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class LdapAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(LdapAuthenticationService.class);

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private LdapAuthenticationProvider ldapAuthenticationProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public AuthResult<?> authenticate(String username, String password, LdapConfiguration ldapConfiguration) {
        Map<String, String> resultMap = getUserDn(username, ldapConfiguration);
        return ldapAuthenticationResult(resultMap.get("result"), password, username, ldapConfiguration);
    }

    private Map<String, String> getUserDn(String username, LdapConfiguration ldapConfiguration) {
        try {
            int index = 0;

            DefaultSpringSecurityContextSource context = ldapAuthenticationProvider.contextSource(ldapConfiguration);
            context.setUserDn(ldapConfiguration.getLdapUserDn());

            ldapTemplate.setContextSource(context);
            ldapTemplate.setDefaultTimeLimit(2500);

            List<String> result = ldapSearchWithoutTimeLimit(username);

            logger.info("[GetDnForUser]: LDAP Search Result: {}", result);

            if (!result.isEmpty()) {
                if (result.size() > 1) {
                    logger.info("[GetDnForUser]: LDAP Search Result >> not unique . . .");
                    throw new NonUniqueResultException(result.size());
                }
                return ImmutableMap.of("result", result.get(0), "index", String.valueOf(index));
            }

            throw new UsernameNotFoundException("User not found: " + username);
        } catch (Exception e) {
            logger.error("[GetDnForUser] ❌: Exception: {}", e.getMessage());
            e.printStackTrace();
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }

    private List<String> ldapSearchWithoutTimeLimit(String username) {
        try {
            return ldapTemplate
                    .search(query().where("mail").is(username).and(query().where("objectClass").is("mailUser"))
                                    .and((query().where("accountStatus").is("active"))),
                            new AbstractContextMapper<String>() {
                                protected String doMapFromContext(DirContextOperations ctx) {
                                    return ctx.getNameInNamespace();
                                }
                            });
        } catch (Exception e) {
            logger.error("[LDAPSearchWithoutTimeLimit] ❌: Exception: {}", e.getMessage());
            e.printStackTrace();
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }

    private AuthResult<?> ldapAuthenticationResult(String userDn, String password, String userName, LdapConfiguration ldapConfig) {
        try {
            ldapAuthenticationProvider.contextSource(ldapConfig).getContext(userDn, password);

            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            if (userDetails != null) {
                UserAccount userAccount = userAccountRepository.findByUsername(userName).orElse(null);
                if (userAccount != null) {
                    userAccount.setPassword(passwordEncoder.encode(password));
                    userAccountRepository.save(userAccount);
                }
            }

            logger.info("[LDAPAuthenticationResult] ✅: {} has successfully authenticated.", userDetails.getUsername());

            return new AuthResult<>(userDetails, "", false, userDetails.getAuthorities());
        } catch (Exception e) {
            logger.error("[LDAPAuthenticationResult] ❌: Exception: {}", e.getMessage());
            return new AuthResult<>(null, e.getMessage(), true, new ArrayList<>());
        }
    }
}
