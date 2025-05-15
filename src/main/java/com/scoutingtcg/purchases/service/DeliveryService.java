package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.dto.CartItemDto;
import com.scoutingtcg.purchases.model.CardForSale;
import com.scoutingtcg.purchases.model.Product;
import com.scoutingtcg.purchases.model.ShippingSize;
import com.scoutingtcg.purchases.repository.CardForSaleRepository;
import com.scoutingtcg.purchases.repository.ProductRepository;

import java.util.*;

public class DeliveryService {

    private final ProductRepository productRepository;

    private final CardForSaleRepository cardForSaleRepository;
    private final AppConfigService appConfigService;

    public DeliveryService(ProductRepository productRepository, CardForSaleRepository cardForSaleRepository, AppConfigService appConfigService) {
        this.productRepository = productRepository;
        this.cardForSaleRepository = cardForSaleRepository;
        this.appConfigService = appConfigService;
    }

    /**
     * Calculates the shipping cost based on the destination state and the items in the cart.
     *
     * @param destinationState The state to which the items are being shipped.
     * @param cardItems        The list of items in the cart.
     * @return The calculated shipping cost.
     */
    public double calculateShippingCost(String destinationState, List<CartItemDto> cardItems) {

        Set<String> nearbyStates = Set.of("VA", "DC", "MD", "DE", "PA", "NJ", "NC", "WV");
        boolean isNearby = nearbyStates.contains(destinationState.toUpperCase());

        boolean freeShippingCards = appConfigService.isFreeShippingForCardsEnabled();
        boolean freeShippingProducts = appConfigService.isFreeShippingForProductsEnabled();

        List<ShippingSize> sizes = new ArrayList<>();

        // 1. Add product sizes (only if there is NO global free shipping)
        if (!freeShippingProducts) {
            List<Product> products = cardItems.stream()
                    .filter(item -> !item.getPresentation().equalsIgnoreCase("single"))
                    .map(item -> productRepository.findById(item.getProductOrCardForSaleId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            for (Product product : products) {
                sizes.add(product.getShippingSize());
            }
        }

        // 2. Add size for card group (only if there is NO global free shipping)
        if (!freeShippingCards) {
            List<CardForSale> cards = cardItems.stream()
                    .filter(item -> item.getPresentation().equalsIgnoreCase("single"))
                    .map(item -> cardForSaleRepository.findById(item.getProductOrCardForSaleId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            if (!cards.isEmpty()) {
                sizes.add(resolveShippingForCardGroup(cards));
            }
        }

        // 3. Determine largest size
        ShippingSize highest = sizes.stream()
                .max(Comparator.comparingInt(this::shippingPriority))
                .orElse(ShippingSize.SINGLE_PLAIN);

        // 4. Calculate cost
        return switch (highest) {
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
