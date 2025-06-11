package com.example.payment_service.dto;

import com.example.payment_service.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    private String orderId;
    private String orderCode;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String returnUrl; // URL để redirect sau khi thanh toán
    private String ipAddress;
    private String userId;
}