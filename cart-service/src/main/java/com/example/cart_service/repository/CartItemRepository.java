package com.example.cart_service.repository;

import com.example.cart_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId);
    void deleteByCartIdAndProductId(String cartId, String productId);
}