package com.scoutingtcg.purchases.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; // optional fallback
    private String fullName;
    private String address;
    private String apartment;
    private String city;
    private String state;
    private String zip;
    private Double total;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Referencia futura al usuario registrado o generado

}