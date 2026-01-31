package com.ecommerce.order.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String status;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private List<ProductDto> products;
    private PaymentDto payment;

    @Data
    public static class PaymentDto {
        private String method;
        private Double amount;
        private String transactionId;
    }
}