// src/main/java/com/ecommerce/order/repository/OrderRepository.java
package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(String status);

    @Query("SELECT o FROM Order o WHERE o.totalAmount > :min")
    List<Order> findByTotalGreaterThan(Double min);
}