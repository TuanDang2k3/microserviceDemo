package com.example.payment_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private String orderCode;
    private String status;
    private BigDecimal totalAmount;
    private String customerEmail;
    private LocalDateTime updatedAt;
    private String reason; // For cancellations or other status changes
}