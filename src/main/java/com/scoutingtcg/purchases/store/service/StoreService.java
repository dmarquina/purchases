package com.scoutingtcg.purchases.store.service;

import com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithPokemonCardDto;
import com.scoutingtcg.purchases.cardforsale.model.CardForSale;
import com.scoutingtcg.purchases.cardforsale.repository.CardForSaleRepository;
import com.scoutingtcg.purchases.pokemoncard.dto.response.PokemonSingleResponse;
import com.scoutingtcg.purchases.pokemoncard.dto.response.PokemonSingleVariantResponse;
import com.scoutingtcg.purchases.pokemoncard.model.PokemonCard;
import com.scoutingtcg.purchases.product.dto.StoreProductResponse;
import com.scoutingtcg.purchases.product.model.Product;
import com.scoutingtcg.purchases.product.repository.ProductRepository;
import com.scoutingtcg.purchases.shared.model.Franchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /**
     * Get a paginated list of sealed products for a given franchise.
     *
     * @param franchise The franchise to get products for.
     * @param pageable  The pagination information.
     * @return A page of sealed products.
     */
    public Page<Product> getSealedProducts(String franchise, Pageable pageable) {
        return productRepository.findByFranchiseAndPresentationNotAndStockGreaterThan(franchise, "Single", pageable, 0);
    }

    /**
     * Get the store products for a given franchise.
     *
     * @param franchise The franchise to get products for.
     * @return A StoreProductResponse containing the products.
     */
    public StoreProductResponse getStoreProducts(Franchise franchise) {
        List<PokemonSingleResponse> pokemonSingleResponseList = new ArrayList<>();
        if (franchise.equals(Franchise.POKEMON)) {
            List<String> top4Ids = cardForSaleRepository.findTop4CardIdsByFranchisePokemon(PageRequest.of(0, 4));
            List<CardForSaleWithPokemonCardDto> variants = cardForSaleRepository.findByCardIdIn(top4Ids);
            pokemonSingleResponseList = getPokemonSingleResponses(variants);
        }

        List<Product> sealedProducts = productRepository.findTop5ByFranchiseAndPresentationNotAndStockGreaterThan(franchise, "Single", 0);

        StoreProductResponse storeProductResponse = new StoreProductResponse();
        storeProductResponse.setSingleProducts(pokemonSingleResponseList);
        storeProductResponse.setSealedProducts(sealedProducts);

        return storeProductResponse;
    }

    private static List<PokemonSingleResponse> getPokemonSingleResponses(List<CardForSaleWithPokemonCardDto> cfsWithPokemonCardDtoList) {
        Map<String, List<CardForSaleWithPokemonCardDto>> grouped = groupByCardId(cfsWithPokemonCardDtoList);

        return grouped.values().stream()
                .map(StoreService::mapToPokemonSingleResponse)
                .toList();
    }

    private static Map<String, List<CardForSaleWithPokemonCardDto>> groupByCardId(List<CardForSaleWithPokemonCardDto> data) {
        return data.stream()
                .collect(Collectors.groupingBy(dto -> dto.getCardForSale().getCardId()));
    }

    private static PokemonSingleResponse mapToPokemonSingleResponse(List<CardForSaleWithPokemonCardDto> groupedDtos) {
        CardForSaleWithPokemonCardDto base = groupedDtos.get(0);
        PokemonCard card = base.getPokemonCard();

        List<PokemonSingleVariantResponse> variants = mapToPokemonSingleVariantResponses(groupedDtos);

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
    }

    private static List<PokemonSingleVariantResponse> mapToPokemonSingleVariantResponses(List<CardForSaleWithPokemonCardDto> groupedDtos) {
        return groupedDtos.stream()
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
    }
}
