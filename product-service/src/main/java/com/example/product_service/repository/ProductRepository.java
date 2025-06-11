package com.example.product_service.repository;

import com.example.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByBrand(String brand);
    List<Product> findByCategory(String category);
    List<Product> findByIsAvailable(boolean isAvailable);
    List<Product> findByNameContainingIgnoreCase(String keyword);
}