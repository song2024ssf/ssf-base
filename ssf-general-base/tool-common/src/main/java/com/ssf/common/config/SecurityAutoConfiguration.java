package com.ssf.common.config;

import com.ssf.common.interceptor.AuthInterceptor;
import com.ssf.common.security.JwtProperties;
import com.ssf.common.security.JwtUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.ssf.common.security")
@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty(prefix = "campus.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityAutoConfiguration {

    @Bean
    public AuthInterceptor authInterceptor(JwtUtils jwtUtils, JwtProperties jwtProperties) {
        return new AuthInterceptor(jwtUtils, jwtProperties);
    }

    // JwtUtils 已在组件扫描范围内，无需显式创建
    // WebMvcConfig 也已通过 @Configuration 自动注册
}