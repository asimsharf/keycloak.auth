package com.sudagoarth.keycloak.auth.DataTransferObjects.user;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String token;
    private String tokenExpirAt;

    // Constructors
    public UserResponse() {
    }

    public UserResponse(Long id, String username, String email, String phone, String token, String tokenExpirAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.token = token;
        this.tokenExpirAt = tokenExpirAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenExpirAt() {
        return tokenExpirAt;
    }

    public void setTokenExpirAt(String tokenExpirAt) {
        this.tokenExpirAt = tokenExpirAt;
    }
}
