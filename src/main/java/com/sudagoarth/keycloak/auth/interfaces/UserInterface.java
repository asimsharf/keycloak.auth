package com.sudagoarth.keycloak.auth.interfaces;

import java.util.Map;
import java.util.Optional;

import com.sudagoarth.keycloak.auth.models.User;

public interface UserInterface {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean isUsernameTaken(String username);

    boolean isEmailTaken(String email);

    User registerUser(User user);

    Optional<Map<String, Object>> loginUser(String username, String password);
}
