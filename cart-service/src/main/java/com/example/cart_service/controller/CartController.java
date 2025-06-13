package com.example.cart_service.controller;

import com.example.cart_service.dto.request.AddItemRequest;
import com.example.cart_service.dto.response.CartResponse;
import com.example.cart_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable String userId) {
        CartResponse cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItemToCart(
            @PathVariable String userId,
            @Valid @RequestBody AddItemRequest request) {
        CartResponse cart = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable String userId,
            @PathVariable String productId,
            @RequestBody Integer quantity) {
        CartResponse cart = cartService.updateCartItem(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable String userId,
            @PathVariable String productId) {
        cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}