package com.scoutingtcg.purchases.dto.Product;

import lombok.Data;

@Data
public class ProductResponse {

    private String id;
    private String cardId;
    private String cardName;
    private String cardImage;
    private String cardSet;
    private String vendorId;
    private Double price;
    private Integer quantity;

    public ProductResponse(String id, String cardId, String cardName, String cardImage, String cardSet,
                           String vendorId, Double price, Integer quantity) {
        this.id = id;
        this.cardId = cardId;
        this.cardName = cardName;
        this.cardImage = cardImage;
        this.cardSet = cardSet;
        this.vendorId = vendorId;
        this.price = price;
        this.quantity = quantity;
    }

}
