package com.scoutingtcg.purchases.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Long productOrCardForSaleId;
    private String name;
    private double price;
    private int quantity;
    private int stock;
    private String image;
    private String presentation;
    private String franchise;
}