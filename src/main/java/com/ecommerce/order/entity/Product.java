package com.ecommerce.order.entity;



import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")  // Note: This table is in product-service DB, but here it's just for JPA mapping/reference
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    private Long id;  // Minimal: only ID for reference (no other attributes to avoid duplication)

    // Bidirectional if needed, but minimal so optional
    @ManyToMany(mappedBy = "products")
    private Set<Order> orders = new HashSet<>();
}