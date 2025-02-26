package com.sudagoarth.keycloak.auth.DataTransferObjects.product;

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

    // Constructors

    public ProductRequest() {
    }

    public ProductRequest(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ProductRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }

}
