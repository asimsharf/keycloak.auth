package com.sudagoarth.keycloak.auth.controller;


import com.sudagoarth.keycloak.auth.ApiResponse;
import com.sudagoarth.keycloak.auth.interfaces.ProductInterface;
import com.sudagoarth.keycloak.auth.model.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductInterface productInterface;

    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@RequestBody Product product) {
    
        // Validate if the product already exists (by name or other unique property)
        boolean isProductExists = productInterface.getAllProducts().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()));
    
        if (isProductExists) {
            // Return 409 Conflict with meaningful error message
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Product already exists", "DUPLICATE_PRODUCT", "The product with this name already exists."));
        }
    
        try {
            // If the product doesn't exist, create it
            Product createdProduct = productInterface.createProduct(product);
    
            // Return 201 Created with the product data
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Product created successfully", createdProduct));
        } catch (Exception e) {
            // Handle unexpected errors gracefully
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error", "SERVER_ERROR", "An error occurred while creating the product."));
        }
    }
    

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", productInterface.getAllProducts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        Product product = productInterface.getProductById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.ok(ApiResponse.error("Product not found", "NOT_FOUND", null));
        } else {
            return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", product));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", productInterface.updateProduct(id, product)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productInterface.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}
