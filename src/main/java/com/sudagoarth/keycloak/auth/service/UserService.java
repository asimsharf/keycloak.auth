package com.sudagoarth.keycloak.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudagoarth.keycloak.auth.model.User;
import com.sudagoarth.keycloak.auth.repository.UserRepository;

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

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

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
            formData.add("grant_type", "client_credentials");
            formData.add("client_id", "spring-client-credentials-id");
            formData.add("client_secret", "9mtG5y9zpsEu9GZ6jDFWOvin74AGVbCC");

            // Create HttpEntity
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

            // Call Keycloak token endpoint
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://localhost:8181/realms/spring-microservices-security-realm/protocol/openid-connect/token",
                    HttpMethod.POST,
                    request,
                    String.class
            );

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

}
