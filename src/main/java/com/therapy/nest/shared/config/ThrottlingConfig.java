package com.therapy.nest.shared.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThrottlingConfig {

    @Bean
    public FilterRegistrationBean<ThrottlingFilter> rateLimitingFilter() {
        FilterRegistrationBean<ThrottlingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ThrottlingFilter());
        registrationBean.addUrlPatterns("/graphql", "/login", "/attachments");
        return registrationBean;
    }
}
