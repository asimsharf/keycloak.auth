package com.sudagoarth.keycloak.auth.interfaces;

import java.util.List;
import java.util.Optional;

import com.sudagoarth.keycloak.auth.DataTransferObjects.product.ProductRequest;
import com.sudagoarth.keycloak.auth.models.Product;


public interface ProductInterface {
    Product createProduct(Product product);

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    Product updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long id);

    List<Product> findProductsByPriceRange(double minPrice, double maxPrice);

    List<Product> searchProductsByName(String keyword);

    boolean existsByName(String name);
}