package com.ecommerce.order.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String method;  // e.g., CREDIT_CARD

    private Double amount;

    private String transactionId;

    // @OneToOne bidirectional
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}