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
