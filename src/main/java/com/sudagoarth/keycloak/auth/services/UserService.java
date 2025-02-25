package com.sudagoarth.keycloak.auth.services;

import java.util.Optional;

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
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

    public User registerUser(User user) {
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public Optional<Map<String, Object>> loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password)); // Use hashed passwords in real apps!

        if (userOpt.isPresent()) {
            try {
                // Set headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                // Set form data
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.add("grant_type", oAuth2GrantType);
                formData.add("client_id", oAuth2ClientId);
                formData.add("client_secret", oAuth2ClientSecret);

                // Create HttpEntity
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

                // Call Keycloak token endpoint
                ResponseEntity<String> response = restTemplate.exchange(
                        oAuth2TokenUri,
                        HttpMethod.POST,
                        request,
                        String.class);

                // Parse the access token from the response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode tokenResponse = mapper.readTree(response.getBody());
                String accessToken = tokenResponse.get("access_token").asText();
                String expiresAt = tokenResponse.get("expires_in").asText();

                // Prepare user data and token to return
                Map<String, Object> result = new HashMap<>();
                result.put("user", userOpt.get());
                result.put("access_token", accessToken);
                result.put("expires_at", expiresAt);

                return Optional.of(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}
