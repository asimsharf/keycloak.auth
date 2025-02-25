package com.sudagoarth.keycloak.auth.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudagoarth.keycloak.auth.models.LocaledData;
import com.sudagoarth.keycloak.auth.models.User;
import com.sudagoarth.keycloak.auth.services.UserService;
import com.sudagoarth.keycloak.auth.util.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        @Autowired
        private UserService userService;

        @Autowired
        private BCryptPasswordEncoder passwordEncoder;

        @PostMapping("/register")
        public ResponseEntity<ApiResponse> register(@RequestBody @Valid User user, BindingResult bindingResult) {
                // Validate the user input
                if (bindingResult.hasErrors()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(new LocaledData(
                                                        "Validation failed",
                                                        "فشل التحقق"), "VALIDATION_FAILED",
                                                        bindingResult.getFieldErrors()));
                }

                // Check if the username or email already exists
                if (userService.isUsernameTaken(user.getUsername())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(new LocaledData(
                                                        "Username already taken",
                                                        "اسم المستخدم موجود بالفعل"), "DUPLICATE_USERNAME", null));
                }

                if (userService.isEmailTaken(user.getEmail())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(new LocaledData(
                                                        "Email already taken",
                                                        "البريد الإلكتروني موجود بالفعل"), "DUPLICATE_EMAIL", null));
                }

                // Register the user
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setEnabled(true);
                User registeredUser = userService.registerUser(user);

                // Return 201 Created with the user's data (excluding sensitive data like
                // password)
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(
                                                new LocaledData(
                                                                "User registered successfully",
                                                                "تم تسجيل المستخدم بنجاح"),
                                                registeredUser));
        }

        @PostMapping("/login")
        public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> credentials) {
                String username = credentials.get("username");
                String password = credentials.get("password");

                // Validate that username and password are provided
                if (username == null || password == null || username.isBlank() || password.isBlank()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(new LocaledData(
                                                        "Missing credentials",
                                                        "بيانات الاعتماد مفقودة"), "MISSING_CREDENTIALS", null));
                }

                // Attempt to log in and retrieve token
                Optional<Map<String, Object>> loginResponse = userService.loginUser(username, password);

                // Handle success and failure
                if (loginResponse.isPresent()) {
                        return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                                        "Login successful",
                                        "تم تسجيل الدخول بنجاح"), loginResponse.get()));
                } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(ApiResponse.error(new LocaledData(
                                                        "Invalid credentials",
                                                        "بيانات الاعتماد غير صالحة"), "INVALID_CREDENTIALS", null));
                }
        }

}
