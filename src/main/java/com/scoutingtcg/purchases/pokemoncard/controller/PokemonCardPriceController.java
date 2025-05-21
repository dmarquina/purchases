package com.scoutingtcg.purchases.pokemoncard.controller;

import com.scoutingtcg.purchases.pokemoncard.service.PokemonCardPriceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prices")
public class PokemonCardPriceController {

    private final PokemonCardPriceService service;

    public PokemonCardPriceController(PokemonCardPriceService service) {
        this.service = service;
    }

    /**
     * This endpoint will be used to update the prices of all the cards stored in the system.
     */
    @PostMapping("/update-pokemon-card-market-prices")
    public void updateCardMarketPrices() {
        service.updateMarketPrices();
    }

    /**
     * Updates the prices of all cards listed for sale in the system.
     */
    @PostMapping("/update-all-card-for-sale")
    public void updateCardForSalePrices() {
        service.updateAllCardForSalePrices();
    }

    /**
     * Updates the prices of cards that are pending and have no price set.
     */
    @PostMapping("/update-no-price-pending-card-for-sale")
    public void updateNoPriceAnPendingCardForSalePrices() {
        service.updateNoPriceAnPendingCardForSalePrices();
    }
}
