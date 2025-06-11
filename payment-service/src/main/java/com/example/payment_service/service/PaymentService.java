package com.example.payment_service.service;

import com.example.payment_service.client.CartServiceClient;
import com.example.payment_service.client.OrderServiceClient;
import com.example.payment_service.dto.PaymentDto;
import com.example.payment_service.dto.PaymentRequestDto;
import com.example.payment_service.entity.Payment;
import com.example.payment_service.entity.PaymentMethod;
import com.example.payment_service.entity.PaymentStatus;
import com.example.payment_service.mapper.PaymentMapper;
import com.example.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final VNPayService vnPayService;
    private final OrderServiceClient orderServiceClient;
    private final CartServiceClient cartServiceClient;
    @Transactional
    public PaymentDto createPayment(PaymentRequestDto requestDto) {
        log.info("Creating payment for order ID: {}, amount: {}",
                requestDto.getOrderId(), requestDto.getAmount());

        // Tạo transaction ID
        String transactionId = UUID.randomUUID().toString();

        // Tạo đối tượng Payment với trạng thái PENDING
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .orderId(requestDto.getOrderId())
                .userId(requestDto.getUserId())
                .orderCode(requestDto.getOrderCode())
                .amount(requestDto.getAmount())
                .status(PaymentStatus.PENDING)
                .paymentMethod(requestDto.getPaymentMethod() != null ?
                        requestDto.getPaymentMethod() : PaymentMethod.VNPAY)
                .userId(requestDto.getUserId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Tạo URL thanh toán cho VNPay
        String paymentUrl = vnPayService.createPaymentUrl(payment, requestDto);
        payment.setPaymentUrl(paymentUrl);

        // Lưu payment vào database
        payment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}, transaction ID: {}", payment.getId(), payment.getTransactionId());

        return paymentMapper.toDto(payment);
    }

    @Transactional
    public PaymentDto processVnpayCallback(Map<String, String> vnpParams) {
        log.info("Processing VNPay callback with parameters: {}", vnpParams);

        // Validate VNPay response
        boolean isValid = vnPayService.validateVnpayReturn(vnpParams);
        if (!isValid) {
            log.error("Invalid VNPay response");
            throw new RuntimeException("Invalid VNPay response");
        }

        String transactionId = vnpParams.get("vnp_TxnRef");
        String vnpResponseCode = vnpParams.get("vnp_ResponseCode");

        // Tìm payment dựa trên transaction ID
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> {
                    log.error("Payment not found with transaction ID: {}", transactionId);
                    return new RuntimeException("Payment not found with transaction ID: " + transactionId);
                });

        // Cập nhật thông tin payment từ VNPay response
        payment.setVnpayTransactionNo(vnpParams.get("vnp_TransactionNo"));
        payment.setBankCode(vnpParams.get("vnp_BankCode"));
        payment.setCardType(vnpParams.get("vnp_CardType"));
        payment.setResponseCode(vnpResponseCode);
        payment.setUpdatedAt(LocalDateTime.now());

        // Cập nhật trạng thái payment dựa trên response code từ VNPay
        if ("00".equals(vnpResponseCode)) {
            // Thanh toán thành công
            payment.setStatus(PaymentStatus.COMPLETED);
            log.info("Payment completed successfully for order: {}", payment.getOrderId());

            // Gọi Order Service để cập nhật trạng thái đơn hàng
            try {
                orderServiceClient.updateOrderStatus(
                        payment.getOrderId(),
                        "PAID",
                        payment.getTransactionId()
                );
                if (payment.getUserId() != null) {
                    try {
                        cartServiceClient.clearCart(payment.getUserId());
                        log.info("🧹 Successfully cleared shopping cart for userId: {}", payment.getUserId());
                    } catch (Exception e) {
                        log.warn("⚠️ Could not clear shopping cart but payment was successful: {}", e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Failed to update order status, but payment was successful", e);
                // Không throw exception, vì thanh toán vẫn thành công
                // Order Service sẽ cần có cơ chế để check và cập nhật trạng thái đơn hàng
            }
        } else {
            // Thanh toán thất bại
            payment.setStatus(PaymentStatus.FAILED);
            log.warn("Payment failed for order: {}, response code: {}",
                    payment.getOrderId(), vnpResponseCode);

            // Gọi Order Service để cập nhật trạng thái đơn hàng thất bại
            try {
                orderServiceClient.updateOrderStatus(
                        payment.getOrderId(),
                        "PAYMENT_FAILED",
                        payment.getTransactionId()
                );
                log.info("Order status updated to PAYMENT_FAILED for order: {}", payment.getOrderId());
            } catch (Exception e) {
                log.error("Failed to update order status for failed payment", e);
            }
        }

        // Lưu payment với thông tin đã cập nhật
        payment = paymentRepository.save(payment);
        log.info("Payment updated with status: {} for transaction ID: {}", payment.getStatus(), payment.getTransactionId());

        return paymentMapper.toDto(payment);
    }

    public List<PaymentDto> getPaymentsByOrderId(String orderId) {
        log.info("Getting payments for order ID: {}", orderId);
        return paymentRepository.findByOrderId(orderId).stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }
}