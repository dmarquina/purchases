package com.scoutingtcg.purchases.store.controller;

import com.scoutingtcg.purchases.product.dto.StoreProductResponse;
import com.scoutingtcg.purchases.shared.model.Franchise;
import com.scoutingtcg.purchases.product.model.Product;
import com.scoutingtcg.purchases.pokemoncard.service.PokemonCardPriceService;
import com.scoutingtcg.purchases.product.service.ProductService;
import com.scoutingtcg.purchases.store.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    public StoreController(StoreService storeService, PokemonCardPriceService pokemonCardPriceService) {
        this.storeService = storeService;
    }

    @GetMapping("/franchise/{franchise}")
    public StoreProductResponse getStoreProducts(@PathVariable Franchise franchise) {
        return storeService.getStoreProducts(franchise);
    }

    @GetMapping("/franchise/{franchise}/sealed")
    public Page<Product> getSealedProducts(
            @PathVariable String franchise,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return storeService.getSealedProducts(franchise, pageable);
    }

}
