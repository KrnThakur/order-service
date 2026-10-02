// src/main/java/com/ecommerce/order/dto/OrderRequest.java
package com.ecommerce.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotBlank
    private String status;

    @Positive
    private Double totalAmount;

    @NotEmpty
    private List<Long> productIds;  // List of product IDs to add

    @NotBlank
    private String paymentMethod;
}