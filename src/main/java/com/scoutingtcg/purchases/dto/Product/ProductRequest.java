package com.scoutingtcg.purchases.dto.Product;

import lombok.Data;

@Data
public class ProductRequest {

    private String id;
    private String cardId;
    private String vendorId;
    private Double price;
    private Integer quantity;
}
