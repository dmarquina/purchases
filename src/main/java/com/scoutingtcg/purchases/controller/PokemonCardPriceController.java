package com.scoutingtcg.purchases.controller;
import com.scoutingtcg.purchases.dto.CardForSale.PokemonCardMarketPriceDto;
import com.scoutingtcg.purchases.service.PokemonCardPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prices")
public class PokemonCardPriceController {

    private final PokemonCardPriceService service;

    public PokemonCardPriceController(PokemonCardPriceService service) {
        this.service = service;
    }

    @PostMapping("/update-pokemon-card-market-prices")
    public void updateCardMarketPrices() {
        service.updateMarketPrices();
    }

    @PostMapping("/update-all-card-for-sale")
    public void updateCardForSalePrices() {
        service.updateAllCardForSalePrices();
    }

    @PostMapping("/update-no-price-pending-card-for-sale")
    public void updateNoPriceAnPendingCardForSalePrices() {
        service.updateNoPriceAnPendingCardForSalePrices();
    }
}
