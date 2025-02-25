package com.sudagoarth.keycloak.auth.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudagoarth.keycloak.auth.models.User;
import com.sudagoarth.keycloak.auth.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${OAUTH2_TOKEN_URI}")
    private String oAuth2TokenUri;

    @Value("${OAUTH2_CLIENT_ID}")
    private String oAuth2ClientId;

    @Value("${OAUTH2_CLIENT_SECRET}")
    private String oAuth2ClientSecret;

    @Value("${OAUTH2_GRANT_TYPE}")
    private String oAuth2GrantType;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${OAUTH2_EXPIRES_IN}")
    private String expires_in;

    public User registerUser(User user) {
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public Optional<Map<String, Object>> loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));

        if (userOpt.isPresent()) {
            try {
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.add("grant_type", oAuth2GrantType);
                formData.add("client_id", oAuth2ClientId);
                formData.add("client_secret", oAuth2ClientSecret);

                Optional<Map<String, String>> tokenResponse = getAccessTokenFromKeycloak(formData);

                // Check if the token response is missing
                if (tokenResponse.isEmpty()) {
                    logger.error("Failed to retrieve access token for user {}", username);
                    return Optional.empty();
                }

                Map<String, Object> result = new HashMap<>();
                tokenResponse.ifPresent(tokenData -> {
                    String accessToken = tokenData.get("access_token");
                    String expiresAt = tokenData.get("expires_in");

                    // Check if expected token fields are available
                    if (accessToken == null || expiresAt == null) {
                        logger.error("Access token or expiration missing for user {}", username);
                        return;
                    }

                    result.put("user", userOpt.get());
                    result.put("access_token", accessToken);
                    result.put("expires_at", expiresAt);

                });

                return Optional.of(result);

            } catch (Exception e) {
                // Log the error in case of an exception
                logger.error("Error occurred while logging in user {}: ", username, e);
            }
        }

        // If no user found or password does not match, return empty Optional
        logger.warn("Invalid login attempt for user {}", username);
        return Optional.empty();
    }

    private Optional<Map<String, String>> getAccessTokenFromKeycloak(MultiValueMap<String, String> formData)
            throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                oAuth2TokenUri,
                HttpMethod.POST,
                request,
                String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode tokenResponse = mapper.readTree(response.getBody());

        if (tokenResponse.has("access_token") && tokenResponse.has("expires_in")) {
            String accessToken = tokenResponse.get("access_token").asText();
            String expiresAt = tokenResponse.get("expires_in").asText();

            logger.info("Access token received: {}", accessToken);
            logger.info("Token expires in: {} seconds", expiresAt);

            Map<String, String> result = new HashMap<>();
            result.put("access_token", accessToken);
            result.put("expires_in", expiresAt); // storing expiration time

            return Optional.of(result);
        } else {
            logger.error("Keycloak response missing required fields.");
            return Optional.empty();
        }
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}
