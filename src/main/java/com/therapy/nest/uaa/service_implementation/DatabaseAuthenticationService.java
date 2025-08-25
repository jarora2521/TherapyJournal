package com.therapy.nest.uaa.service_implementation;

import com.therapy.nest.shared.utils.AuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class DatabaseAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuthenticationService.class);

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResult<?> authenticate(String username, String password) {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            logger.error("[DatabaseAuthenticationService] ❌: User {} not found.", username);
            return new AuthResult<>(null, "Incorrect Login Credentials", true, authorities);
        }

        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            logger.info("[DatabaseAuthenticationService] ✅: Successfully authenticated user {}.", username);

            authorities = userDetails.getAuthorities();

            return new AuthResult<>(userDetails, "", false, authorities);
        } else {
            logger.error("[DatabaseAuthenticationService] ❌: Incorrect password for user {}.", username);
            return new AuthResult<>(null, "Incorrect Login Credentials", true, authorities);
        }
    }
}
