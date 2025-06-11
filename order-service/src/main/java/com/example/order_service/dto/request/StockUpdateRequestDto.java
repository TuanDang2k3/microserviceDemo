package com.example.order_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequestDto {
    private String productId;
    private Integer quantity;
    private String operation; // "REDUCE" or "INCREASE"
}