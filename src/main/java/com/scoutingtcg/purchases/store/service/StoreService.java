package com.scoutingtcg.purchases.store.service;

import com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithPokemonCardDto;
import com.scoutingtcg.purchases.cardforsale.model.CardForSale;
import com.scoutingtcg.purchases.cardforsale.repository.CardForSaleRepository;
import com.scoutingtcg.purchases.pokemoncard.dto.PokemonSingleResponse;
import com.scoutingtcg.purchases.pokemoncard.dto.PokemonSingleVariantResponse;
import com.scoutingtcg.purchases.pokemoncard.model.PokemonCard;
import com.scoutingtcg.purchases.product.dto.StoreProductResponse;
import com.scoutingtcg.purchases.product.model.Product;
import com.scoutingtcg.purchases.product.repository.ProductRepository;
import com.scoutingtcg.purchases.shared.model.Franchise;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private final ProductRepository productRepository;
    private final CardForSaleRepository cardForSaleRepository;

    public StoreService(ProductRepository productRepository,
                        CardForSaleRepository cardForSaleRepository) {
        this.productRepository = productRepository;
        this.cardForSaleRepository = cardForSaleRepository;
    }

    public StoreProductResponse getStoreProducts(Franchise franchise) {
        List<PokemonSingleResponse> pokemonSingleResponseList = new ArrayList<>();
        if (franchise.equals(Franchise.POKEMON)) {
            List<String> top5Ids = cardForSaleRepository.findTop4CardIdsByFranchisePokemon(PageRequest.of(0, 4));
            List<CardForSaleWithPokemonCardDto> variants = cardForSaleRepository.findByCardIdIn(top5Ids);
            pokemonSingleResponseList = getPokemonSingleResponses(variants);
        }

        List<Product> sealedProducts = productRepository.findTop5ByFranchiseAndPresentationNotAndStockGreaterThan(franchise, "Single", 0);

        StoreProductResponse storeProductResponse = new StoreProductResponse();
        storeProductResponse.setSingleProducts(pokemonSingleResponseList);
        storeProductResponse.setSealedProducts(sealedProducts);

        return storeProductResponse;
    }

    private static List<PokemonSingleResponse> getPokemonSingleResponses(List<CardForSaleWithPokemonCardDto> data) {
        Map<String, List<CardForSaleWithPokemonCardDto>> grouped = data.stream()
                .collect(Collectors.groupingBy(dto -> dto.getCardForSale().getCardId()));

        return grouped.entrySet().stream()
                .map(entry -> {
                    CardForSaleWithPokemonCardDto base = entry.getValue().get(0);
                    PokemonCard card = base.getPokemonCard();

                    List<PokemonSingleVariantResponse> variants = entry.getValue().stream()
                            .map(dto -> {
                                CardForSale cfs = dto.getCardForSale();
                                return new PokemonSingleVariantResponse(
                                        cfs.getId(),
                                        cfs.getCardCondition(),
                                        cfs.getPrinting(),
                                        cfs.getFranchise().name(),
                                        cfs.getPrice(),
                                        cfs.getStock()
                                );
                            }).toList();

                    return new PokemonSingleResponse(
                            card.getId(),
                            card.getName(),
                            card.getImageUrl(),
                            card.getSetId().toUpperCase() + ": " + card.getSetName(),
                            card.getRarity(),
                            card.getNumber(),
                            Franchise.POKEMON.name(),
                            variants
                    );
                })
                .toList();
    }
}
