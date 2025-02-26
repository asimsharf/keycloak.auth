package com.sudagoarth.keycloak.auth.controllers;

import com.sudagoarth.keycloak.auth.DataTransferObjects.product.ProductRequest;
import com.sudagoarth.keycloak.auth.DataTransferObjects.product.ProductResponse;
import com.sudagoarth.keycloak.auth.exceptions.products.ProductNotFoundException;
import com.sudagoarth.keycloak.auth.interfaces.ProductInterface;
import com.sudagoarth.keycloak.auth.models.LocaledData;
import com.sudagoarth.keycloak.auth.models.Product;
import com.sudagoarth.keycloak.auth.util.ApiResponse;

import jakarta.validation.Valid;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

        @Autowired
        private ProductInterface productInterface;

        private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

        @PostMapping
        public ResponseEntity<ApiResponse> createProduct(@RequestBody @Valid ProductRequest productRequest,
                        BindingResult bindingResult) {

                // Handle validation errors properly
                if (bindingResult.hasErrors()) {

                        List<FieldError> validationErrors = bindingResult.getFieldErrors();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(new LocaledData("Validation failed", "فشل التحقق"),
                                                        "VALIDATION_FAILED", validationErrors));
                }

                // Check if the product already exists
                if (productInterface.existsByName(productRequest.getName())) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                        .body(ApiResponse.error(
                                                        new LocaledData("Product already exists",
                                                                        "المنتج موجود بالفعل"),
                                                        "DUPLICATE_PRODUCT", null));
                }

                try {
                        // Convert DTO to Product entity
                        Product product = new Product();
                        product.setName(productRequest.getName());
                        product.setDescription(productRequest.getDescription());
                        product.setPrice(productRequest.getPrice());

                        // Create product
                        Product createdProduct = productInterface.createProduct(product);

                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(ApiResponse.success(new LocaledData("Product created successfully",
                                                        "تم إنشاء المنتج بنجاح"), createdProduct));
                } catch (Exception e) {
                        logger.error("An error occurred while creating the product", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error(
                                                        new LocaledData("An error occurred while creating the product",
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
public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id, 
                                                 @RequestBody @Valid ProductRequest productRequest, 
                                                 BindingResult bindingResult) {
    
    // Handle validation errors
    if (bindingResult.hasErrors()) {
        List<FieldError> validationErrors = bindingResult.getFieldErrors();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(new LocaledData("Validation failed", "فشل التحقق"),
                        "VALIDATION_FAILED", validationErrors));
    }

    try {
        // Delegate update logic to the service layer
        Product updatedProduct = productInterface.updateProduct(id, productRequest);

        // Convert entity to response DTO
        ProductResponse productResponse = ProductResponse.fromEntity(updatedProduct);

        return ResponseEntity.ok(ApiResponse.success(new LocaledData(
                "Product updated successfully", "تم تحديث المنتج بنجاح"), productResponse));

    } catch (ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(new LocaledData("Product not found", "المنتج غير موجود"),
                        "NOT_FOUND", null));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new LocaledData("An error occurred while updating the product",
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
                                                        "لا يمكن أن تكون الكلمة المفتاحية فارغة"), "INVALID_INPUT",
                                                        null));
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
