package com.sudagoarth.keycloak.auth.interfaces;

import com.sudagoarth.keycloak.auth.models.Product;
import java.util.List;

public interface CustomProductInterface {
    List<Product> findProductsByPriceRange(double minPrice, double maxPrice);
    List<Product> searchProductsByName(String keyword);
}