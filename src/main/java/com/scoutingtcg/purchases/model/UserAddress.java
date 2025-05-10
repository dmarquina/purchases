package com.scoutingtcg.purchases.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "user_addresses")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String fullName;
    private String addressLine;
    private String apartment; // opcional
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Boolean isDefault; // opcional: true si es la dirección principal

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
