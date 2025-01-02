package com.kapital.assignment.message_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

@Configuration
public class EncryptionServiceFeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && StringUtils.hasText(authentication.getCredentials().toString())) {
                    String token = authentication.getCredentials().toString();
                    template.header("Authorization", "Bearer " + token);
                }
            }
        };
    }
}
