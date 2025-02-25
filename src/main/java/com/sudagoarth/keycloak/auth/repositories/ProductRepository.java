package com.sudagoarth.keycloak.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sudagoarth.keycloak.auth.interfaces.CustomProductInterface;
import com.sudagoarth.keycloak.auth.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductInterface {
    
}
