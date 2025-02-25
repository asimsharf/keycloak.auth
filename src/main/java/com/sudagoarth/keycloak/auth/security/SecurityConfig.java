package com.sudagoarth.keycloak.auth.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudagoarth.keycloak.auth.util.ApiAuthentication;

@Configuration
public class SecurityConfig {

        @Value("${OAUTH2_JWK_URI}")
        private String jwkUri;

        @Bean
        public JwtDecoder jwtDecoder() {
                return NimbusJwtDecoder.withJwkSetUri(jwkUri).build();
        }

        @Bean
        public RestTemplate restTemplate() {
                return new RestTemplate();
        }

        @Bean
        public ApiAuthentication apiAuthentication() {
                return new ApiAuthentication(new ObjectMapper());
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity.csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                                                .requestMatchers("/actuator/**").permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.decoder(jwtDecoder())))
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .authenticationEntryPoint(apiAuthentication()))
                                .build();
        }
}
