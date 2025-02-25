package com.sudagoarth.keycloak.auth.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    @Size(max = 255, message = "Description should not exceed 255 characters")
    private String description;

    @Positive(message = "Price must be positive")
    private double price;
}
