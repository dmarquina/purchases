package com.scoutingtcg.purchases.dto.CardForSale;

import lombok.Data;

@Data
public class PriceEntry {
    private Double market;

    public Double getMarket() {
        return market;
    }
}