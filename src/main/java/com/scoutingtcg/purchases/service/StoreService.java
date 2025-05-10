package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.dto.CardForSale.CardForSaleWithPokemonCardDto;
import com.scoutingtcg.purchases.dto.CardForSale.PokemonSingleResponse;
import com.scoutingtcg.purchases.dto.CardForSale.PokemonSingleVariantResponse;
import com.scoutingtcg.purchases.dto.Product.StoreProductResponse;
import com.scoutingtcg.purchases.model.CardForSale;
import com.scoutingtcg.purchases.model.Franchise;
import com.scoutingtcg.purchases.model.PokemonCard;
import com.scoutingtcg.purchases.model.Product;
import com.scoutingtcg.purchases.repository.CardForSaleRepository;
import com.scoutingtcg.purchases.repository.ProductRepository;
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

    public StoreProductResponse getStoreProducts(String franchise) {
        List<PokemonSingleResponse> pokemonSingleResponseList = new ArrayList<>();
        if(franchise.equals(Franchise.POKEMON.name())){
            List<String> top5Ids = cardForSaleRepository.findTop4CardIdsByFranchisePokemon(PageRequest.of(0, 4));
            List<CardForSaleWithPokemonCardDto> variants = cardForSaleRepository.findByCardIdIn(top5Ids);
            pokemonSingleResponseList = getPokemonSingleResponses(variants);
        }

        List<Product> sealedProducts = productRepository.findTop5ByFranchiseAndPresentationNot(franchise, "Single");

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

                    return PokemonSingleResponse.builder()
                            .cardId(card.getId())
                            .cardName(card.getName())
                            .imageUrl(card.getImageUrl())
                            .setName(card.getSetId().toUpperCase() + ": " + card.getSetName())
                            .rarity(card.getRarity())
                            .number(card.getNumber())
                            .franchise(Franchise.POKEMON.name())
                            .variants(variants)
                            .build();
                })
                .toList();
    }
}
