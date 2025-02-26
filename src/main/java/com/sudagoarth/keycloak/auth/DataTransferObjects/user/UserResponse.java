package com.sudagoarth.keycloak.auth.DataTransferObjects.user;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String accessToken;
    private String expiresIn;

    // Constructors
    public UserResponse() {
    }

    public UserResponse(Long id, String username, String email, String phone, String accessToken, String expiresIn) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
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
        return accessToken;
    }

    public void setToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenExpirAt() {
        return expiresIn;
    }

    public void setTokenExpirAt(String expiresIn) {
        this.expiresIn = expiresIn;
    }
}
