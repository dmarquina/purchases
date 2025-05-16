package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.dto.CardForSale.CardForSaleWithPokemonCardDto;
import com.scoutingtcg.purchases.dto.CardForSale.PokemonCardApiResponse;
import com.scoutingtcg.purchases.dto.CardForSale.PokemonCardMarketPriceDto;
import com.scoutingtcg.purchases.model.CardForSale;
import com.scoutingtcg.purchases.model.pokemon.PokemonCardMarketPrice;
import com.scoutingtcg.purchases.repository.CardForSaleRepository;
import com.scoutingtcg.purchases.repository.PokemonCardMarketPriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PokemonCardPriceService {

    private static final String API_BASE_URL = "https://api.pokemontcg.io/v2/cards";
    private static final int PAGE_SIZE = 250;
    private static final String API_KEY = "09b181b2-b529-4e1a-938d-f3e24e4809e1";

    private final PokemonCardMarketPriceRepository priceRepository;
    private final CardForSaleRepository cardForSaleRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = LoggerFactory.getLogger(PokemonCardPriceService.class);

    public PokemonCardPriceService(
            PokemonCardMarketPriceRepository priceRepository,
            CardForSaleRepository cardForSaleRepository
    ) {
        this.priceRepository = priceRepository;
        this.cardForSaleRepository = cardForSaleRepository;
    }

    public void updateAllCardForSalePrices() {
        List<CardForSale> cardForSales = cardForSaleRepository.findAll();
        updateCardForSaleWithLatestPrices(cardForSales);
    }

    public void updateNoPriceAnPendingCardForSalePrices() {
        Set<String> setIds = extractSetIds(cardForSaleRepository.findNoPriceAndPendingCardForSaleWithPokemonCard());
        List<PokemonCardMarketPriceDto> prices = getMarketPricesBySetIds(setIds);
        savePricesToDatabase(prices);

        List<CardForSale> cardForSales = cardForSaleRepository.findByPriceGreaterThanZeroAndStatusPending();
        updateCardForSaleWithLatestPrices(cardForSales);
    }

    public void setCardForSalePrices(List<CardForSale> cardForSales) {
        updateCardForSaleWithLatestPrices(cardForSales);
    }

    public void updateMarketPrices() {
        try {
            Set<String> setIds = extractSetIds(cardForSaleRepository.findAllCardForSaleWithPokemonCard());
            List<PokemonCardMarketPriceDto> prices = getMarketPricesBySetIds(setIds);
            savePricesToDatabase(prices);

        } catch (Exception e) {
            logger.error("Failed to update market prices", e);
            throw new RuntimeException("Failed to update market prices", e);
        }
    }

    private List<PokemonCardMarketPriceDto> getMarketPricesBySetIds(Set<String> setIds) {
        List<PokemonCardMarketPriceDto> allPrices = new ArrayList<>();

        int page = 1;
        int totalPages;

        do {
            String url = buildApiUrl(setIds, page);
            ResponseEntity<PokemonCardApiResponse> response = fetchApiResponse(url);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new IllegalStateException("Failed to fetch Pokémon TCG data. Status: " + response.getStatusCode());
            }
            PokemonCardApiResponse body = Objects.requireNonNull(response.getBody(), "Pokémon TCG API response is null.");

            List<PokemonCardMarketPriceDto> pagePrices = extractPricesFromResponse(body);
            allPrices.addAll(pagePrices);

            totalPages = (int) Math.ceil((double) body.getTotalCount() / PAGE_SIZE);
            page++;

        } while (page <= totalPages);

        return allPrices;
    }

    private Set<String> extractSetIds(List<CardForSaleWithPokemonCardDto> data) {
        return data.stream()
                .map(dto -> dto.getPokemonCard().getSetId())
                .collect(Collectors.toSet());
    }

    private String buildApiUrl(Set<String> setIds, int page) {
        String setQuery = setIds.stream()
                .map(setId -> "set.id:" + setId)
                .collect(Collectors.joining(" OR "));

        return UriComponentsBuilder.fromHttpUrl(API_BASE_URL)
                .queryParam("q", "(" + setQuery + ")")
                .queryParam("page", page)
                .queryParam("pageSize", PAGE_SIZE)
                .build()
                .toUriString();
    }

    private ResponseEntity<PokemonCardApiResponse> fetchApiResponse(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", API_KEY);
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PokemonCardApiResponse.class
        );
    }

    private List<PokemonCardMarketPriceDto> extractPricesFromResponse(PokemonCardApiResponse response) {
        return response.getData().stream()
                .map(card -> {
                    Map<String, Double> priceMap = new HashMap<>();

                    if (card.getTcgplayer() != null && card.getTcgplayer().getPrices() != null) {
                        card.getTcgplayer().getPrices().forEach((type, entry) -> {
                            if (entry.getMarket() != null) {
                                priceMap.put(type, entry.getMarket());
                            }
                        });
                    } else {
                        ensureEmptyPriceEntryExists(card.getId());
                    }

                    return new PokemonCardMarketPriceDto(card.getId(), priceMap);
                })
                .toList();
    }

    private void ensureEmptyPriceEntryExists(String cardId) {
        if (priceRepository.findById(cardId).isEmpty()) {
            PokemonCardMarketPrice emptyPrice = new PokemonCardMarketPrice();
            emptyPrice.setCardId(cardId);
            emptyPrice.setPrices(new HashMap<>());
            emptyPrice.setUpdatedAt(LocalDate.now());
            priceRepository.save(emptyPrice);
        }
    }

    private void savePricesToDatabase(List<PokemonCardMarketPriceDto> dtos) {
        List<PokemonCardMarketPrice> entities = dtos.stream()
                .map(this::mapToEntity)
                .toList();

        priceRepository.saveAll(entities);
    }

    private void updateCardForSaleWithLatestPrices(List<CardForSale> cardForSales) {

        List<PokemonCardMarketPrice> cardMarketPricesStored = priceRepository.findAllById(
                cardForSales.stream()
                        .map(CardForSale::getCardId)
                        .collect(Collectors.toSet())
        );

        Map<String, Map<String, Double>> priceMapByCardId = cardMarketPricesStored.stream()
                .collect(Collectors.toMap(PokemonCardMarketPrice::getCardId, PokemonCardMarketPrice::getPrices));

        for (CardForSale cardForSale : cardForSales) {
            String cardId = cardForSale.getCardId();
            if (priceMapByCardId.containsKey(cardId)) {
                Map<String, Double> pricesMap = priceMapByCardId.get(cardId);
                String cardPrinting = cardForSale.getPrinting().toLowerCase();
                if (pricesMap != null && pricesMap.keySet().stream().anyMatch(key -> key.contains(cardPrinting))) {
                    Double price = pricesMap.get(cardPrinting);
                    if (price != null && price > 0 && cardForSale.getPrice() < price) {
                        cardForSale.setPrice(Math.floor(price * 0.95 * 100.0) / 100.0);
                    }
                }


            }
        }

        cardForSaleRepository.saveAll(cardForSales);
    }

    private PokemonCardMarketPrice mapToEntity(PokemonCardMarketPriceDto dto) {
        PokemonCardMarketPrice entity = new PokemonCardMarketPrice();
        entity.setCardId(dto.getId());
        entity.setPrices(dto.getTcgPlayerPrices());
        entity.setUpdatedAt(LocalDate.now());
        return entity;
    }

    //@Scheduled(cron = "0 0 3 * * MON")
    public void scheduledPriceUpdate() {
        //updateMarketPricesAndCardForSale();
    }
}
