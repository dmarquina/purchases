package com.scoutingtcg.purchases.order.model;

import com.scoutingtcg.purchases.shared.model.ShippingSize;
import com.scoutingtcg.purchases.security.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private String id = UUID.randomUUID().toString();

    private String email;
    private String phone;
    private String fullName;
    private String address;
    private String apartment;
    private String city;
    private String state;
    private String zip;
    private Double total;
    private String receiptUrl;
    private double shippingCost;
    private boolean freeShippingApplied;

    @Enumerated(EnumType.STRING)
    private ShippingSize shippingSize;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}