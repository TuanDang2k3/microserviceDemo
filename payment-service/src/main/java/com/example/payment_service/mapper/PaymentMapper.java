package com.example.payment_service.mapper;

import com.example.payment_service.dto.PaymentDto;
import com.example.payment_service.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .orderCode(payment.getOrderCode())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .paymentUrl(payment.getPaymentUrl())
                .responseCode(payment.getResponseCode())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}