package com.therapy.nest.uaa.service_implementation;

import com.therapy.nest.uaa.entity.Oauth2RegisteredClient;
import com.therapy.nest.uaa.entity.UserAccount;
import com.therapy.nest.uaa.repository.Oauth2RegisteredClientRepository;
import com.therapy.nest.uaa.repository.UserAccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private Oauth2RegisteredClientRepository oauth2RegisteredClientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Load by username: {} at: {}", username, LocalDateTime.now());

        Optional<UserAccount> userAccount = userAccountRepository.findByUsername(username);
        if (userAccount.isPresent()) {
            Collection<? extends GrantedAuthority> authorities = getAuthorities(userAccount.get());
            userAccount.get().setAuthorities(authorities);
        }

        return userAccount.orElse(null);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(UserAccount userAccount) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (userAccount.getRole() != null) {
            userAccount.getRole().getPermissions().forEach(rolePermission -> {
                authorities.add(new SimpleGrantedAuthority(rolePermission.getPermission().getName()));
            });
        }
        return authorities;
    }

    public Map<String, Object> getAttributes(String principalName) {
        UserAccount user = userAccountRepository.findByUsername(principalName).orElse(null);

        Map<String, Object> attributes = new HashMap<>();
        ObjectMapper objMap = new ObjectMapper();

        if (user == null) {
            Optional<Oauth2RegisteredClient> clientRegistration = oauth2RegisteredClientRepository.findByClientId(principalName);
            if (clientRegistration.isPresent()) {
                attributes.put("client", principalName);
            }
        } else {
            attributes = objMap.convertValue(user, Map.class);
        }

        return attributes;
    }

    public Collection<GrantedAuthority> getAuthoritiesByPrincipalName(String principalName) {
        UserAccount account = userAccountRepository.findByUsername(principalName).orElse(null);
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (account != null) {
            account.getRole().getPermissions().forEach(rolePermission -> {
                authorities.add(new SimpleGrantedAuthority(rolePermission.getPermission().getName()));
            });
        }

        return authorities;
    }
}
