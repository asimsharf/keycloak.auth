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

import com.sudagoarth.keycloak.auth.interfaces.UserInterface;
import com.sudagoarth.keycloak.auth.models.LocaledData;
import com.sudagoarth.keycloak.auth.models.User;
import com.sudagoarth.keycloak.auth.util.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        @Autowired
        private UserInterface userInterface;

        @Autowired
        private BCryptPasswordEncoder passwordEncoder;

        @PostMapping("/register")
        public ResponseEntity<ApiResponse> register(@RequestBody @Valid User user, BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(new LocaledData("Validation failed", "فشل التحقق"),
                                                        "VALIDATION_FAILED", bindingResult.getFieldErrors()));
                }

                if (userInterface.isUsernameTaken(user.getUsername())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(
                                                        new LocaledData("Username already taken",
                                                                        "اسم المستخدم موجود بالفعل"),
                                                        "DUPLICATE_USERNAME", null));
                }

                if (userInterface.isEmailTaken(user.getEmail())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(
                                                        new LocaledData("Email already taken",
                                                                        "البريد الإلكتروني موجود بالفعل"),
                                                        "DUPLICATE_EMAIL", null));
                }

                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setEnabled(true);
                User registeredUser = userInterface.registerUser(user);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(new LocaledData("User registered successfully",
                                                "تم تسجيل المستخدم بنجاح"), registeredUser));
        }

        @PostMapping("/login")
        public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> credentials) {
                String username = credentials.get("username");
                String password = credentials.get("password");

                if (username == null || password == null || username.isBlank() || password.isBlank()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(
                                                        new LocaledData("Missing credentials",
                                                                        "بيانات الاعتماد مفقودة"),
                                                        "MISSING_CREDENTIALS", null));
                }

                Optional<Map<String, Object>> loginResponse = userInterface.loginUser(username, password);
                return loginResponse.map(response -> ResponseEntity.ok(ApiResponse
                                .success(new LocaledData("Login successful", "تم تسجيل الدخول بنجاح"), response)))
                                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(ApiResponse.error(
                                                                new LocaledData("Invalid credentials",
                                                                                "بيانات الاعتماد غير صالحة"),
                                                                "INVALID_CREDENTIALS", null)));
        }

}
