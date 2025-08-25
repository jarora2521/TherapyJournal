package com.therapy.nest.shared.config;

import com.therapy.nest.shared.enums.GeneralConfigurationEnum;
import com.therapy.nest.shared.repository.GeneralConfigurationRepository;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class ThrottlingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ThrottlingFilter.class);

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    static private GeneralConfigurationRepository generalConfigurationRepository;

    @Autowired
    public void init(GeneralConfigurationRepository generalConfigurationRepository) {
        ThrottlingFilter.generalConfigurationRepository = generalConfigurationRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String ip = httpRequest.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, k -> createNewBucket());
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            httpResponse.setHeader("X-Rate-Limit-Remaining", "" + probe.getRemainingTokens());
            filterChain.doFilter(request, response);
        } else {
            String jsonResponse = "{\"error\": \"Too many requests\"}";
            httpResponse.setHeader("X-Rate-Limit-Retry-After-Seconds", "" + TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
            httpResponse.setContentType("application/json");
            httpResponse.setStatus(429);
            httpResponse.getWriter().write(jsonResponse);

            logger.warn("[ThrottlingFilter] ❌: Throttling limit reached for user: {} under IP: {}", getLoggedInUser(), ip);
        }
    }

    private Bucket createNewBucket() {
        Long maxTokens = getBucketConfigurationValue(GeneralConfigurationEnum.MAXIMUM_THROTTLE_TOKENS);
        Long refillTokens = getBucketConfigurationValue(GeneralConfigurationEnum.THROTTLE_REFILL_TOKENS);
        Long refillDuration = getBucketConfigurationValue(GeneralConfigurationEnum.THROTTLE_REFILL_DURATION);

        return Bucket.builder()
                .addLimit(limit -> limit.capacity(maxTokens).refillGreedy(refillTokens, Duration.ofSeconds(refillDuration)))
                .build();
    }

    private Long getBucketConfigurationValue(GeneralConfigurationEnum config) {
        String configValue = generalConfigurationRepository.findConfigValueByName(config.toString()).orElse(null);
        if (configValue != null) {
            return Long.parseLong(configValue);
        }
        throw new NullPointerException(String.format("[GetBucketConfigurationValue]: Configuration: %s not found", config));
    }

    public String getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "ANONYMOUS";
    }
}
