package com.sudagoarth.keycloak.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudagoarth.keycloak.auth.ApiResponse;
import com.sudagoarth.keycloak.auth.model.User;
import com.sudagoarth.keycloak.auth.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody User user) {
        return ResponseEntity.ok(new ApiResponse("User registered successfully", 0, userService.registerUser(user), null, false, null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        return userService.loginUser(username, password)
                .map(data -> ResponseEntity.ok(new ApiResponse("User logged in successfully", 0, data, null, true, null)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Invalid credentials", 1, null, null, false, null)));
    }
}
