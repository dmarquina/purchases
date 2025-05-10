package com.scoutingtcg.purchases.dto.CardForSale;

import lombok.Data;

import java.util.Map;

@Data
public class TcgPlayer {
    private Map<String, PriceEntry> prices;

    public Map<String, PriceEntry> getPrices() {
        return prices;
    }
}