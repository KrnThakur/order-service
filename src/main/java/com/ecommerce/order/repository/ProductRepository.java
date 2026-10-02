// src/main/java/com/ecommerce/order/repository/ProductRepository.java
package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}