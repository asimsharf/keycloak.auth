package com.sudagoarth.keycloak.auth.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudagoarth.keycloak.auth.DataTransferObjects.user.UserRequest;
import com.sudagoarth.keycloak.auth.DataTransferObjects.user.UserResponse;
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
        public ResponseEntity<ApiResponse> register(@RequestBody @Valid UserRequest userRequest,
                        BindingResult bindingResult) {
                // Handle validation errors properly
                if (bindingResult.hasErrors()) {
                        List<FieldError> validationErrors = bindingResult.getFieldErrors();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(new LocaledData("Validation failed", "فشل التحقق"),
                                                        HttpStatus.BAD_REQUEST.value(),
                                                        "VALIDATION_FAILED", validationErrors));
                }

                // Check if username or email is already taken
                if (userInterface.isUsernameTaken(userRequest.getUsername())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(
                                                        new LocaledData("Username already taken",
                                                                        "اسم المستخدم موجود بالفعل"),
                                                        HttpStatus.BAD_REQUEST.value(),
                                                        "DUPLICATE_USERNAME", null));
                }

                if (userInterface.isEmailTaken(userRequest.getEmail())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(
                                                        new LocaledData("Email already taken",
                                                                        "البريد الإلكتروني موجود بالفعل"),
                                                        HttpStatus.BAD_REQUEST.value(),
                                                        "DUPLICATE_EMAIL", null));
                }

                // Create new User entity
                User user = new User();
                user.setUsername(userRequest.getUsername());
                user.setEmail(userRequest.getEmail());
                user.setPhone(userRequest.getPhone());
                user.setPassword(passwordEncoder.encode(userRequest.getPassword())); // Encrypt password
                user.setEnabled(true);

                // Register the user
                User registeredUser = userInterface.registerUser(user);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(new LocaledData("User registered successfully",

                                                "تم تسجيل المستخدم بنجاح"), HttpStatus.CREATED.value(),
                                                registeredUser));
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
                                                        HttpStatus.BAD_REQUEST.value(),
                                                        "MISSING_CREDENTIALS", null));
                }

                Optional<Map<String, Object>> loginResponse = userInterface.loginUser(username, password);

                return loginResponse.map(response -> {
                        User user = (User) response.get("user");
                        String token = (String) response.get("access_token");
                        String expiresIn = (String) response.get("expires_in");
                

                        UserResponse userResponse = new UserResponse(
                                        user.getId(),
                                        user.getUsername(),
                                        user.getEmail(),
                                        user.getPhone(),
                                        token,
                                        expiresIn);

                        return ResponseEntity.ok(ApiResponse
                                        .success(new LocaledData("Login successful", "تم تسجيل الدخول بنجاح"),
                                                        HttpStatus.OK.value(),
                                                        userResponse));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error(
                                                new LocaledData("Invalid credentials", "بيانات الاعتماد غير صالحة"),
                                                HttpStatus.UNAUTHORIZED.value(),
                                                "INVALID_CREDENTIALS", null)));
        }

}
