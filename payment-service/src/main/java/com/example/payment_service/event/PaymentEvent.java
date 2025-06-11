package com.example.payment_service.event;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String transactionId;
    private String orderId;
    private String orderCode;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private String responseCode;
}