package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.dto.ShippingCalculateRequest;
import com.scoutingtcg.purchases.dto.ShippingSummary;
import com.scoutingtcg.purchases.model.CardForSale;
import com.scoutingtcg.purchases.model.Product;
import com.scoutingtcg.purchases.model.ShippingSize;
import com.scoutingtcg.purchases.repository.CardForSaleRepository;
import com.scoutingtcg.purchases.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShippingService {

    private final ProductRepository productRepository;
    private final CardForSaleRepository cardForSaleRepository;
    private final AppConfigService appConfigService;

    public ShippingService(ProductRepository productRepository,
                           CardForSaleRepository cardForSaleRepository,
                           AppConfigService appConfigService) {
        this.productRepository = productRepository;
        this.cardForSaleRepository = cardForSaleRepository;
        this.appConfigService = appConfigService;
    }

    /**
     * Calculates the shipping cost based on the provided request.
     *
     * @param request the shipping calculation request containing destination state and cart items
     * @return a summary of the shipping cost, including the cost, packaging size, and whether free shipping was applied
     */
    public ShippingSummary calculateShippingCost(ShippingCalculateRequest request) {
        Set<String> nearbyStates = Set.of("VA", "DC", "MD", "DE", "PA", "NJ", "NC", "WV");
        boolean isNearby = nearbyStates.contains(request.destinationState().toUpperCase());

        boolean freeShippingCards = appConfigService.isFreeShippingForCardsEnabled();
        boolean freeShippingProducts = appConfigService.isFreeShippingForProductsEnabled();

        // Retrieve products and cards (always, because size always matters)
        List<Product> products = request.cartItems().stream()
                .filter(item -> !item.getPresentation().equalsIgnoreCase("single"))
                .map(item -> productRepository.findById(item.getProductOrCardForSaleId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        List<CardForSale> cards = request.cartItems().stream()
                .filter(item -> item.getPresentation().equalsIgnoreCase("single"))
                .map(item -> cardForSaleRepository.findById(item.getProductOrCardForSaleId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        // Determine relevant packaging sizes
        List<ShippingSize> sizes = new ArrayList<>();

        for (Product product : products) {
            sizes.add(product.getShippingSize());
        }

        if (!cards.isEmpty()) {
            sizes.add(resolveShippingForCardGroup(cards));
        }

        // Determine the largest packaging size
        ShippingSize highest = sizes.stream()
                .max(Comparator.comparingInt(this::shippingPriority))
                .orElse(ShippingSize.SINGLE_PLAIN);

        boolean freeShippingApplied = freeShippingCards && freeShippingProducts;
        double shippingCost = freeShippingApplied ? 0.0 : calculateShippingCostBySize(highest, isNearby);

        return new ShippingSummary(shippingCost, highest, freeShippingApplied);
    }

    private double calculateShippingCostBySize(ShippingSize size, boolean isNearby) {
        return switch (size) {
            case SINGLE_PLAIN -> 1.31;
            case SINGLE_BUBBLE -> isNearby ? 4.00 : 5.00;
            case PRODUCT_SMALL -> isNearby ? 5.00 : 6.00;
            case PRODUCT_MEDIUM -> isNearby ? 8.00 : 9.00;
            case PRODUCT_LARGE -> isNearby ? 12.00 : 13.50;
        };
    }

    private ShippingSize resolveShippingForCardGroup(List<CardForSale> cards) {
        double totalValue = cards.stream()
                .mapToDouble(CardForSale::getPrice)
                .sum();
        return totalValue >= 50.0 ? ShippingSize.SINGLE_BUBBLE : ShippingSize.SINGLE_PLAIN;
    }

    private int shippingPriority(ShippingSize size) {
        return switch (size) {
            case SINGLE_PLAIN -> 1;
            case SINGLE_BUBBLE -> 2;
            case PRODUCT_SMALL -> 3;
            case PRODUCT_MEDIUM -> 4;
            case PRODUCT_LARGE -> 5;
        };
    }
}
