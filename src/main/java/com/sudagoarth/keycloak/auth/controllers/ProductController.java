package com.sudagoarth.keycloak.auth.controllers;

import com.sudagoarth.keycloak.auth.interfaces.ProductInterface;
import com.sudagoarth.keycloak.auth.models.LocaledData;
import com.sudagoarth.keycloak.auth.models.Product;
import com.sudagoarth.keycloak.auth.util.ApiResponse;

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
                    .body(ApiResponse.error(new LocaledData(
                            "Product already exists",
                            "المنتج موجود بالفعل"), "DUPLICATE_PRODUCT", null));
        }

        try {
            // If the product doesn't exist, create it
            Product createdProduct = productInterface.createProduct(product);

            // Return 201 Created with the product data
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(new LocaledData(
                            "Product created successfully",
                            "تم إنشاء المنتج بنجاح"), createdProduct));
        } catch (Exception e) {
            // Handle unexpected errors gracefully
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            new LocaledData(
                                    "An error occurred while creating the product",
                                    "حدث خطأ أثناء إنشاء المنتج"),
                            "SERVER_ERROR", null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                "Products retrieved successfully",
                "تم استرداد المنتجات بنجاح"), productInterface.getAllProducts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        Product product = productInterface.getProductById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.ok(ApiResponse.error(new LocaledData(
                    "Product not found",
                    "المنتج غير موجود"), "NOT_FOUND", null));
        } else {
            return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                    "Product retrieved successfully",
                    "تم استرداد المنتج بنجاح"), product));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                "Product updated successfully",
                "تم تحديث المنتج بنجاح"), productInterface.updateProduct(id, product)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productInterface.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(
                new LocaledData(
                        "Product deleted successfully",
                        "تم حذف المنتج بنجاح"),
                null));
    }
}
