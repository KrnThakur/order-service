// src/main/java/com/ecommerce/order/service/OrderService.java
package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.ProductDto;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.Payment;
import com.ecommerce.order.entity.Product;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.PaymentRepository;
import com.ecommerce.order.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductClient productClient;
    private final EntityManager entityManager;
    private final ProductRepository productRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderResponse createOrder(OrderRequest request) {

        List<Product> products = new ArrayList<>();
        double calculatedTotal = 0.0;

        for (Long productId : request.getProductIds()) {
            ProductDto dto = productClient.getProductById(productId);
            if (dto.getStockQuantity() <= 0) {
                throw new RuntimeException("Out of stock for product: " + dto.getName());
            }
            // Decrement stock via Feign (in tx, rollback if fails)
            productClient.updateStock(productId, dto.getStockQuantity() - 1);

            Product product = productRepository.findById(productId).orElseGet(() -> {
                Product newProduct = Product.builder().id(productId).build();
                return productRepository.save(newProduct);
            });
            products.add(product);
            calculatedTotal += dto.getPrice();
        }

        if (calculatedTotal != request.getTotalAmount()) {
            throw new RuntimeException("Total mismatch");
        }

        Order order = Order.builder()
                .status(request.getStatus())
                .totalAmount(request.getTotalAmount())
                .orderDate(LocalDateTime.now())
                .products(new HashSet<>(products))
                .build();

        // Create payment (@OneToOne)
        Payment payment = Payment.builder()
                .method(request.getPaymentMethod())
                .amount(request.getTotalAmount())
                .transactionId("TX-" + System.currentTimeMillis())
                .order(order)
                .build();
        order.setPayment(payment);

        order = orderRepository.save(order);
        log.info("Order created with id: {}", order.getId());
        return mapToResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<OrderResponse> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)  // New tx for isolation in high-load (1000 req/s)
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(request.getStatus());

        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    // Criteria API - with joins for payment and products
    public List<OrderResponse> searchOrders(String status, Double minTotal) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);

        // Join payment
        Join<Order, Payment> paymentJoin = root.join("payment");

        List<Predicate> predicates = new ArrayList<>();

        if (status != null && !status.isBlank()) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        if (minTotal != null) {
            predicates.add(cb.greaterThan(root.get("totalAmount"), minTotal));
        }

        // Example join condition (e.g., payment method)
        predicates.add(cb.equal(paymentJoin.get("method"), "CREDIT_CARD"));  // Demo

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("orderDate")));

        return entityManager.createQuery(cq)
                .getResultList()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        List<ProductDto> productDtos = order.getProducts().stream()
                .map(p -> productClient.getProductById(p.getId()))  // Fetch details via Feign
                .collect(Collectors.toList());

        OrderResponse.PaymentDto paymentDto = new OrderResponse.PaymentDto();
        paymentDto.setMethod(order.getPayment().getMethod());
        paymentDto.setAmount(order.getPayment().getAmount());
        paymentDto.setTransactionId(order.getPayment().getTransactionId());

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .products(productDtos)
                .payment(paymentDto)
                .build();
    }
}