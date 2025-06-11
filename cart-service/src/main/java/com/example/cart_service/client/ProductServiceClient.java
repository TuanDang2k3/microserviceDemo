package com.example.cart_service.client;

import com.example.cart_service.dto.request.ApiResponse;
import com.example.cart_service.dto.response.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;

import javax.naming.ServiceUnavailableException;

@Component
public class ProductServiceClient {

    private final WebClient webClient;
    private final Logger log = LoggerFactory.getLogger(ProductServiceClient.class);

    public ProductServiceClient(WebClient.Builder webClientBuilder) {
        System.out.println("=== Khởi tạo ProductServiceClient ===");
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8088/products") // Đảm bảo port này chính xác
                .build();
        System.out.println("=== Đã khởi tạo WebClient với baseUrl: http://localhost:8088/products ===");
    }

    public ProductResponse getProduct(String productId) throws ServiceUnavailableException {
        System.out.println("=================================================================");
        System.out.println("===== BẮT ĐẦU GỌI PRODUCT SERVICE với productId: " + productId + " =====");
        System.out.println("=================================================================");
        try {
            log.info("Fetching product with ID: {}", productId);
            System.out.println("URL đầy đủ: http://localhost:8088/products/" + productId);

            System.out.println("Trước khi gọi webClient.get()");
            var mono = webClient.get()
                    .uri("/{productId}", productId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<ProductResponse>>() {});
            System.out.println("Sau khi tạo mono, trước khi block()");

            var response = mono.block();
            System.out.println("Đã gọi xong product service và nhận được response");

            if (response == null) {
                System.out.println("CẢNH BÁO: Response là NULL");
                log.error("Product not found: {}", productId);
                throw new ServiceUnavailableException("Product not found: " + productId);
            }

            System.out.println("Response code: " + response.getCode());
            System.out.println("Response message: " + response.getMessage());
            System.out.println("Response result null? " + (response.getResult() == null));

            if (response.getResult() == null) {
                System.out.println("CẢNH BÁO: Response.result là NULL");
                log.error("Product not found: {}", productId);
                throw new ServiceUnavailableException("Product not found: " + productId);
            }

            ProductResponse product = response.getResult();
            System.out.println("Product ID: " + product.getId());
            System.out.println("Product Name: " + product.getName());
            System.out.println("Product Price: " + product.getPrice());
            System.out.println("Product Available: " + product.isAvailable());

            log.info("Product found: {}", response.getResult());
            System.out.println("=================================================================");
            System.out.println("===== KẾT THÚC GỌI PRODUCT SERVICE - THÀNH CÔNG =====");
            System.out.println("=================================================================");
            return product;

        } catch (Exception e) {
            System.out.println("=================================================================");
            System.out.println("===== LỖI KHI GỌI PRODUCT SERVICE =====");
            System.out.println("Loại lỗi: " + e.getClass().getName());
            System.out.println("Thông báo lỗi: " + e.getMessage());
            System.out.println("Stack trace:");
            e.printStackTrace();
            System.out.println("=================================================================");

            log.error("Error fetching product: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("Could not connect to product service: " + e.getMessage());
        }
    }
}