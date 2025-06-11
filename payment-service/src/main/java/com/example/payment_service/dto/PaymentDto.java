package com.example.payment_service.dto;

import com.example.payment_service.entity.PaymentMethod;
import com.example.payment_service.entity.PaymentStatus;
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
public class PaymentDto {
    private String id;
    private String transactionId;
    private String orderId;
    private String userId;
    private String orderCode;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String paymentUrl;
    private String responseCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}