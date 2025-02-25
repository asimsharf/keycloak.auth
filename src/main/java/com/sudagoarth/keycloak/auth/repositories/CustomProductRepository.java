package com.sudagoarth.keycloak.auth.repositories;

import com.sudagoarth.keycloak.auth.interfaces.CustomProductInterface;
import com.sudagoarth.keycloak.auth.models.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomProductRepository implements CustomProductInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> findProductsByPriceRange(double minPrice, double maxPrice) {
        String queryStr = "SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max";
        TypedQuery<Product> query = entityManager.createQuery(queryStr, Product.class);
        query.setParameter("min", minPrice);
        query.setParameter("max", maxPrice);
        return query.getResultList();
    }

    @Override
    public List<Product> searchProductsByName(String keyword) {
        String queryStr = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:keyword)";
        TypedQuery<Product> query = entityManager.createQuery(queryStr, Product.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }
}
