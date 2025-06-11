package com.example.cart_service.service;

import com.example.cart_service.client.ProductServiceClient;
import com.example.cart_service.dto.request.AddItemRequest;
import com.example.cart_service.dto.response.CartItemResponse;
import com.example.cart_service.dto.response.CartResponse;
import com.example.cart_service.dto.response.ProductResponse;
import com.example.cart_service.entity.Cart;
import com.example.cart_service.entity.CartItem;
import com.example.cart_service.repository.CartItemRepository;
import com.example.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;

    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(Cart.builder().userId(userId).items(new ArrayList<>()).build());

        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse addItemToCart(String userId, AddItemRequest request) {
        System.out.println("==================================================");
        System.out.println("BẮT ĐẦU THÊM SẢN PHẨM VÀO GIỎ HÀNG");
        System.out.println("UserId: " + userId);
        System.out.println("ProductId: " + request.getProductId());
        System.out.println("Quantity: " + request.getQuantity());

        // Fetch product information
        ProductResponse product = null;
        try {
            System.out.println("Trước khi gọi productServiceClient.getProduct()");
            product = productServiceClient.getProduct(request.getProductId());
            System.out.println("Sau khi gọi productServiceClient.getProduct()");
            System.out.println("Product đã lấy được: " + (product != null));

            if (product != null) {
                System.out.println("Product details:");
                System.out.println("- ID: " + product.getId());
                System.out.println("- Name: " + product.getName());
                System.out.println("- Price: " + product.getPrice());
                System.out.println("- Available: " + product.isAvailable());
            }

        } catch (ServiceUnavailableException e) {
            System.out.println("LỖI ServiceUnavailableException: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("LỖI không xác định: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi không xác định: " + e.getMessage());
        }

        if (product == null) {
            System.out.println("CẢNH BÁO: Product là NULL");
            throw new RuntimeException("Product not found");
        }

        if (!product.isAvailable()) {
            System.out.println("CẢNH BÁO: Product is not available (available = false)");
            throw new RuntimeException("Product is not available");
        }

        System.out.println("Sản phẩm hợp lệ, tiếp tục xử lý...");

        // Get or create cart
        System.out.println("Tìm hoặc tạo giỏ hàng cho user: " + userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(Cart.builder().userId(userId).items(new ArrayList<>()).build());
        System.out.println("CartId: " + cart.getId());

        // Check if item already exists in cart
        System.out.println("Kiểm tra sản phẩm đã tồn tại trong giỏ hàng chưa");
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            System.out.println("Sản phẩm đã tồn tại trong giỏ hàng, cập nhật số lượng");
            CartItem item = existingItem.get();
            int oldQuantity = item.getQuantity();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setPrice(product.getPrice());
            System.out.println("Số lượng cũ: " + oldQuantity + " -> Số lượng mới: " + item.getQuantity());
        } else {
            // Create new cart item
            System.out.println("Thêm sản phẩm mới vào giỏ hàng");
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .build();
            cart.getItems().add(newItem);
            System.out.println("Đã thêm sản phẩm mới vào giỏ hàng");
        }

        System.out.println("Lưu giỏ hàng vào database");
        cart = cartRepository.save(cart);
        System.out.println("Đã lưu giỏ hàng vào database");

        CartResponse response = mapToCartResponse(cart);
        System.out.println("KẾT THÚC THÊM SẢN PHẨM VÀO GIỎ HÀNG - THÀNH CÔNG");
        System.out.println("==================================================");
        return response;
    }

    @Transactional
    public CartResponse updateCartItem(String userId, String productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (quantity <= 0) {
            cart.getItems().remove(itemToUpdate);
        } else {
            itemToUpdate.setQuantity(quantity);
        }

        cart = cartRepository.save(cart);
        return mapToCartResponse(cart);
    }

    @Transactional
    public void removeItemFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalPrice(totalPrice)
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        ProductResponse product = null;
        try {
            product = productServiceClient.getProduct(item.getProductId());
        } catch (ServiceUnavailableException e) {
            throw new RuntimeException(e);
        }
        BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(product != null ? product.getName() : "Unknown")
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(subtotal)
                .build();
    }
}