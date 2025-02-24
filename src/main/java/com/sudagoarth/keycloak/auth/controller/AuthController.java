package com.sudagoarth.keycloak.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudagoarth.keycloak.auth.ApiResponse;
import com.sudagoarth.keycloak.auth.model.User;
import com.sudagoarth.keycloak.auth.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid User user, BindingResult bindingResult) {
        // Validate the user input
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid input", "VALIDATION_FAILED", bindingResult.getFieldErrors()));
        }

        // Check if the username or email already exists
        if (userService.isUsernameTaken(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Username already taken", "DUPLICATE_USERNAME", null));
        }

        if (userService.isEmailTaken(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email already taken", "DUPLICATE_EMAIL", null));
        }

        // Register the user
        User registeredUser = userService.registerUser(user);

        // Return 201 Created with the user's data (excluding sensitive data like password)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Validate credentials and return a token or session
        return userService.loginUser(username, password)
                .map(token -> ResponseEntity.ok(ApiResponse.success("User logged in successfully", token)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid credentials", "INVALID_CREDENTIALS", null)));
    }
}
