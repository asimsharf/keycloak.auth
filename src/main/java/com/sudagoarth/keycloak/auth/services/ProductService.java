package com.sudagoarth.keycloak.auth.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sudagoarth.keycloak.auth.exceptions.ProductNotFoundException;
import com.sudagoarth.keycloak.auth.interfaces.ProductInterface;
import com.sudagoarth.keycloak.auth.models.Product;
import com.sudagoarth.keycloak.auth.repositories.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements ProductInterface  {
    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Override
    public Product createProduct(Product product) {
        logger.info("Creating product: {}", product.getName());

        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    @Override
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @Override
    public List<Product> findProductsByPriceRange(double minPrice, double maxPrice) {
        return productRepository.findProductsByPriceRange(minPrice, maxPrice);
    }

    @Override
    public List<Product> searchProductsByName(String keyword) {
        return productRepository.searchProductsByName(keyword);
    }
}
