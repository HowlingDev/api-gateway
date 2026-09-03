package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AddLoginHeaderFilter filter) {
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        http.oauth2Login(Customizer.withDefaults());
        return http
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers("/error", "/login/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterAfter(filter, BearerTokenAuthenticationFilter.class)
                .build();
    }
}
