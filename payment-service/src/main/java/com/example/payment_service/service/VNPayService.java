package com.example.payment_service.service;

import com.example.payment_service.dto.PaymentRequestDto;
import com.example.payment_service.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${vnpay.api-url}")
    private String vnpApiUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    @Value("${vnpay.version}")
    private String vnpVersion;

    public String createPaymentUrl(Payment payment, PaymentRequestDto request) {
        try {
            // Các thông số cơ bản
            String vnp_Command = "pay";
            String orderType = "190000"; // Default for general payment
            String bankCode = "";  // Để trống để người dùng chọn ngân hàng

            // Tạo mã tham chiếu giao dịch
            String vnp_TxnRef = payment.getTransactionId();
            String vnp_IpAddr = request.getIpAddress() != null ? request.getIpAddress() : "127.0.0.1";

            // Tạo map chứa các tham số
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnpVersion);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnpTmnCode);

            // Số tiền phải nhân với 100 (VNPay yêu cầu số tiền tính theo đơn vị xu)
            long amountInCents = payment.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue();
            vnp_Params.put("vnp_Amount", String.valueOf(amountInCents));

            vnp_Params.put("vnp_CurrCode", "VND");

            if (bankCode != null && !bankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }

            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Payment for order " + payment.getOrderCode());
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", "vn");

            // Sử dụng returnUrl từ request nếu có, ngược lại sử dụng giá trị mặc định
            String returnUrl = request.getReturnUrl() != null && !request.getReturnUrl().isEmpty()
                    ? request.getReturnUrl()
                    : vnpReturnUrl;

            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            // Ngày tạo giao dịch
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            // Ngày hết hạn giao dịch (15 phút sau khi tạo)
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // Sắp xếp các tham số theo thứ tự
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            // Tạo chuỗi hash data và query
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();

            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // Build hash data
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                            .append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            // Tạo HMAC-SHA512 signature
            String queryUrl = query.toString();
            String vnp_SecureHash = hmacSHA512(vnpHashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

            // URL thanh toán hoàn chỉnh
            return vnpApiUrl + "?" + queryUrl;

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error creating payment URL: " + e.getMessage(), e);
        }
    }

    public boolean validateVnpayReturn(Map<String, String> vnpParams) {
        // Lấy secure hash từ response
        String vnp_SecureHash = vnpParams.get("vnp_SecureHash");
        if (vnp_SecureHash == null) {
            return false;
        }

        // Loại bỏ các tham số không cần thiết
        Map<String, String> cleanParams = new HashMap<>(vnpParams);
        cleanParams.remove("vnp_SecureHash");
        cleanParams.remove("vnp_SecureHashType");

        // Sắp xếp các tham số
        List<String> fieldNames = new ArrayList<>(cleanParams.keySet());
        Collections.sort(fieldNames);

        // Tạo chuỗi hash data để xác thực
        try {
            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = cleanParams.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }

            // Tính toán hash và so sánh
            String calculatedHash = hmacSHA512(vnpHashSecret, hashData.toString());
            return calculatedHash.equals(vnp_SecureHash);

        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    private String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}