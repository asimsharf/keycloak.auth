package com.sudagoarth.keycloak.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sudagoarth.keycloak.auth.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
