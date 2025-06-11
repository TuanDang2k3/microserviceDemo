package com.example.payment_service.controller;

import com.example.payment_service.dto.PaymentDto;
import com.example.payment_service.dto.PaymentRequestDto;
import com.example.payment_service.entity.PaymentStatus;
import com.example.payment_service.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentDto> createPayment(
            @RequestBody PaymentRequestDto requestDto,
            HttpServletRequest request) {

        // Lấy địa chỉ IP của client
        String ipAddress = getClientIp(request);
        log.info("Creating payment for order ID: {} from IP: {}", requestDto.getOrderId(), ipAddress);

        // Đặt địa chỉ IP vào request
        requestDto.setIpAddress(ipAddress);

        // Gọi service để tạo payment
        PaymentDto response = paymentService.createPayment(requestDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay/callback")
    public ResponseEntity<String> vnpayCallback(@RequestParam Map<String, String> parameters) {
        log.info("Received VNPay callback with parameters: {}", parameters);

        // Xử lý callback từ VNPay
        PaymentDto paymentDto = paymentService.processVnpayCallback(parameters);

        // Trả về trang kết quả thanh toán
//        if (PaymentStatus.COMPLETED.name().equals(paymentDto.getStatus().name())) {
//
//            return ResponseEntity.ok("Thanh toán thành công! Mã đơn hàng: " + paymentDto.getOrderCode());
//        } else {
//            return ResponseEntity.ok("Thanh toán thất bại. Mã phản hồi: " + paymentDto.getResponseCode());
//        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "http://localhost:8080/orders");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByOrderId(@PathVariable String orderId) {
        log.info("Getting payments for order ID: {}", orderId);
        List<PaymentDto> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Lấy địa chỉ IP thực từ request
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}