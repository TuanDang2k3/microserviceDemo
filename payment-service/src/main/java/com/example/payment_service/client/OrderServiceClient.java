package com.example.payment_service.client;

import com.example.payment_service.dto.OrderStatusUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class OrderServiceClient {

    private final RestTemplate restTemplate;
    private final String orderServiceBaseUrl;

    public OrderServiceClient(RestTemplate restTemplate,
                              @Value("${services.order.url:http://localhost:8083}") String orderServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.orderServiceBaseUrl = orderServiceBaseUrl;
    }

    public void updateOrderStatus(String orderId, String status, String transactionId) {
        try {
            // Tạo URL với query parameters
            String url = UriComponentsBuilder.fromHttpUrl(orderServiceBaseUrl + "/orders/" + orderId + "/status")
                    .queryParam("status", status)
                    .queryParam("transactionId", transactionId)
                    .toUriString();

            log.info("Updating order status for orderId: {}, status: {}, URL: {}",
                    orderId, status, url);

            // Gọi API với POST và không có body
            restTemplate.postForEntity(url, null, Void.class);
            log.info("Successfully updated order status");
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage(), e);
            // Không throw exception để tránh ảnh hưởng đến luồng thanh toán
        }
    }

}