package com.scoutingtcg.purchases.controller;

import com.scoutingtcg.purchases.dto.CardForSale.PokemonCardMarketPriceDto;
import com.scoutingtcg.purchases.dto.Product.StoreProductResponse;
import com.scoutingtcg.purchases.model.Product;
import com.scoutingtcg.purchases.service.PokemonCardPriceService;
import com.scoutingtcg.purchases.service.ProductService;
import com.scoutingtcg.purchases.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final ProductService productService;
    private final StoreService storeService;
    public StoreController(ProductService productService, StoreService storeService, PokemonCardPriceService pokemonCardPriceService) {
        this.productService = productService;
        this.storeService = storeService;
    }

    @GetMapping("/franchise/{franchise}")
    public StoreProductResponse getStoreProducts(@PathVariable String franchise) {
        return storeService.getStoreProducts(franchise);
    }


    @GetMapping("/franchise/{franchise}/sealed")
    public Page<Product> getSealedProducts(
            @PathVariable String franchise,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getSealedProducts(franchise, pageable);
    }

}
