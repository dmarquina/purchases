package com.scoutingtcg.purchases.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private String email;
    private String fullName;
    private String address;
    private String apartment;
    private String city;
    private String state;
    private String zip;
    private Double total;
    private List<CartItemDto> cartItems;
}