package com.example.payment_service.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class CartServiceClient {

    private final RestTemplate restTemplate;
    private final String cartServiceUrl;

    public CartServiceClient(RestTemplate restTemplate,
                             @Value("${services.cart.url:http://localhost:8082/carts/}") String cartServiceUrl) {
        this.restTemplate = restTemplate;
        this.cartServiceUrl = cartServiceUrl;
        log.info("CartServiceClient initialized with URL: {}", cartServiceUrl);
    }

    /**
     * Xóa toàn bộ giỏ hàng của người dùng
     */
    public void clearCart(String userId) {
        try {
            log.info("🧹 CART SERVICE - Clearing cart for user: {}", userId);
            String url = cartServiceUrl + userId;
            log.debug("DELETE request to URL: {}", url);

            restTemplate.delete(url);

            log.info("✅ CART SERVICE - Cart cleared successfully for user: {}", userId);
        } catch (RestClientException e) {
            log.error("❌ CART SERVICE ERROR clearing cart for userId: {}", userId, e);

        }
    }
}