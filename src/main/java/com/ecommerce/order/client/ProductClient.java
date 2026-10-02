// src/main/java/com/ecommerce/order/client/ProductClient.java
package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", url = "${product-service.url}",path = "/api/products")//fallback , configuration ,
public interface ProductClient {

    @GetMapping("{id}")
    ProductDto getProductById(@PathVariable Long id);

    // To update stock (simple PUT for decrement)
    @PutMapping("{id}/stock")
    void updateStock(@PathVariable Long id, @RequestBody Integer newStock);
}