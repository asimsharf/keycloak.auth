package com.sudagoarth.keycloak.auth.controllers;

import com.sudagoarth.keycloak.auth.DataTransferObjects.ProductResponse;
import com.sudagoarth.keycloak.auth.interfaces.ProductInterface;
import com.sudagoarth.keycloak.auth.models.LocaledData;
import com.sudagoarth.keycloak.auth.models.Product;
import com.sudagoarth.keycloak.auth.util.ApiResponse;

import java.util.List;

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
            // Convert all Product entities to ProductResponse
            List<ProductResponse> productResponse = productInterface.getAllProducts()
                    .stream()
                    .map(ProductResponse::fromEntity)
                    .toList();
        
            // Return the list with a success response
            return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                    "Products retrieved successfully",
                    "تم استرداد المنتجات بنجاح"), productResponse));
        }
        

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
                Product product = productInterface.getProductById(id).orElse(null);

                if (product == null) {
                        return ResponseEntity.ok(ApiResponse.error(new LocaledData(
                                        "Product not found",
                                        "المنتج غير موجود"), "NOT_FOUND", null));
                } else {
                        ProductResponse productResponse = ProductResponse.fromEntity(product);
                        return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                                        "Product retrieved successfully",
                                        "تم استرداد المنتج بنجاح"), productResponse));
                }
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
                try {
                        // Update product in the service layer and fetch the updated version
                        Product product = productInterface.updateProduct(id, updatedProduct);

                        // Convert the updated entity to a response DTO
                        ProductResponse productResponse = ProductResponse.fromEntity(product);

                        // Return success response
                        return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                                        "Product updated successfully",
                                        "تم تحديث المنتج بنجاح"), productResponse));

                } catch (RuntimeException e) {
                        // If product not found or other errors occur
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                        ApiResponse.error(new LocaledData(
                                                        "Product not found",
                                                        "المنتج غير موجود"), "NOT_FOUND", null));
                } catch (Exception e) {
                        // Handle other unexpected exceptions
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                        ApiResponse.error(new LocaledData(
                                                        "An error occurred while updating the product",
                                                        "حدث خطأ أثناء تحديث المنتج"), "SERVER_ERROR", null));
                }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
                try {
                        // Delete the product by ID
                        productInterface.deleteProduct(id);

                        // Return success response
                        return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                                        "Product deleted successfully",
                                        "تم حذف المنتج بنجاح"), null));
                } catch (RuntimeException e) {
                        // If product not found
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                        ApiResponse.error(new LocaledData(
                                                        "Product not found",
                                                        "المنتج غير موجود"), "NOT_FOUND", null));
                } catch (Exception e) {
                        // Handle other unexpected exceptions
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                        ApiResponse.error(new LocaledData(
                                                        "An error occurred while deleting the product",
                                                        "حدث خطأ أثناء حذف المنتج"), "SERVER_ERROR", null));
                }
        }

        @GetMapping("/search")
        public ResponseEntity<ApiResponse> searchProducts(@RequestParam String keyword) {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(new LocaledData(
                                "Keyword cannot be empty",
                                "لا يمكن أن تكون الكلمة المفتاحية فارغة"), "INVALID_INPUT", null));
            }
        
            List<ProductResponse> productResponse = productInterface.searchProductsByName(keyword)
                    .stream()
                    .map(ProductResponse::fromEntity)
                    .toList();
        
            return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                    "Products found",
                    "تم العثور على المنتجات"), productResponse));
        }
        
        @GetMapping("/price-range")
        public ResponseEntity<ApiResponse> getProductsByPriceRange(@RequestParam double min, @RequestParam double max) {
            if (min < 0 || max < 0 || min > max) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(new LocaledData(
                                "Invalid price range",
                                "نطاق السعر غير صالح"), "INVALID_PRICE_RANGE", null));
            }
        
            List<ProductResponse> productResponse = productInterface.findProductsByPriceRange(min, max)
                    .stream()
                    .map(ProductResponse::fromEntity)
                    .toList();
        
            return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                    "Products within price range",
                    "المنتجات ضمن نطاق السعر"), productResponse));
        }
        

}
