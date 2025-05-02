package com.scoutingtcg.purchases.controller;
import com.scoutingtcg.purchases.model.PokemonCard;
import com.scoutingtcg.purchases.service.PokemonCardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class PokemonCardController {

    private final PokemonCardService service;

    public PokemonCardController(PokemonCardService service) {
        this.service = service;
    }

    @GetMapping("/cards/ordered")
    public void getOrderedCards() {
        System.out.println("Fetching ordered cards...");
    }
}
