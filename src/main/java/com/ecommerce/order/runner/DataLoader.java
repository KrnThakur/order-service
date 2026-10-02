// src/main/java/com/ecommerce/order/runner/DataLoader.java
package com.ecommerce.order.runner;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final OrderService orderService;

    @Override
    public void run(String... args) throws Exception {
        if (orderService.getAllOrders().isEmpty()) {
            log.info("Inserting initial order data...");

            // Assume product IDs from ProductService (1,2,3)
            OrderRequest req1 = new OrderRequest();
            req1.setStatus("PENDING");
            req1.setTotalAmount(999.99 + 2399.00);
            req1.setProductIds(Arrays.asList(1L, 2L));
            req1.setPaymentMethod("CREDIT_CARD");
            orderService.createOrder(req1);

            OrderRequest req2 = new OrderRequest();
            req2.setStatus("COMPLETED");
            req2.setTotalAmount(179.99);
            req2.setProductIds(Arrays.asList(3L));
            req2.setPaymentMethod("PAYPAL");
            orderService.createOrder(req2);

            log.info("Initial orders inserted successfully!");
        }
    }
}