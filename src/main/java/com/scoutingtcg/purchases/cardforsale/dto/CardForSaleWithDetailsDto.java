package com.scoutingtcg.purchases.cardforsale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardForSaleWithDetailsDto {
    private Long id;
    private String printing;
    private String name;
    private String number;
    private String rarity;
    private String setName;


}