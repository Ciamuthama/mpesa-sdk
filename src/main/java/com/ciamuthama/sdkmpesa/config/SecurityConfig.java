package com.ciamuthama.sdkmpesa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.api-key}")
    private String apiKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Stateless API, CSRF not applicable
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Callback from Safaricom - authenticated by the ApiKeyFilter but with a
                        // separate path
                        .requestMatchers("/api/v1/callback").permitAll()
                        // Actuator health endpoint
                        .requestMatchers("/actuator/health").permitAll()
                        // OpenAPI docs
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // MCP endpoints - allow (MCP has its own auth mechanism)
                        .requestMatchers("/mcp/**", "/sse/**").permitAll()
                        .requestMatchers("/sse", "/mcp/message").permitAll()
                        // All other endpoints require API key
                        .anyRequest().authenticated())
                .addFilterBefore(new ApiKeyAuthFilter(apiKey), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
