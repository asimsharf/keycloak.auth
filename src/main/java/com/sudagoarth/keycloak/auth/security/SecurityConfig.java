package com.sudagoarth.keycloak.auth.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import com.sudagoarth.keycloak.auth.exceptions.ApiAuthentication;

/**
 * Security configuration class for setting up authentication and authorization.
 */
@Configuration
public class SecurityConfig {

        private final ApiAuthentication apiAuthentication;

        /**
         * Constructor injection for the custom authentication entry point.
         */
        public SecurityConfig(ApiAuthentication apiAuthentication) {
                this.apiAuthentication = apiAuthentication;
        }

        /**
         * URL for fetching JWT public keys from Keycloak.
         * Loaded from application properties or environment variables.
         */
        @Value("${OAUTH2_JWK_URI}")
        private String jwkUri;

        /**
         * Provides a RestTemplate bean for making HTTP requests.
         */
        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
                return builder.build();
        }

        /**
         * Configures JWT decoder to validate tokens using Keycloak's public keys.
         */
        @Bean
        public JwtDecoder jwtDecoder() {
                return NimbusJwtDecoder.withJwkSetUri(jwkUri).build();
        }

        /**
         * Provides a BCrypt password encoder for encrypting passwords.
         */
        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * Configures security settings, including authentication and authorization
         * rules.
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                                                .requestMatchers("/actuator/**").permitAll()
                                                .anyRequest().authenticated())
                                                .httpBasic(http -> http.disable()) // Disable basic authentication
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.decoder(jwtDecoder())) // Use JWT-based authentication
                                )
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint(apiAuthentication) // ✅ Ensure custom entry
                                                                                             // point is used
                                )
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless
                                                                                                        // session
                                )
                                .formLogin(form -> form.disable()) // Disable default login form
                                .build();
        }

}
