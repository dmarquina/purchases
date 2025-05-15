package com.scoutingtcg.purchases.dto;

import com.scoutingtcg.purchases.model.ShippingSize;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private String email;
    private String fullName;
    private String address;
    private String apartment;
    private String phone;
    private String city;
    private String state;
    private String zip;
    private double shippingCost;
    private boolean freeShippingApplied;
    private ShippingSize shippingSize;
    private Double total;
    private List<CartItemDto> cartItems;
}