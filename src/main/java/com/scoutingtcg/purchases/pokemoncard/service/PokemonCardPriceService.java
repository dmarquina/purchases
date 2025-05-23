package com.scoutingtcg.purchases.pokemoncard.service;

import com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithPokemonCardDto;
import com.scoutingtcg.purchases.cardforsale.model.CardForSale;
import com.scoutingtcg.purchases.pokemoncard.dto.api.PokemonCardApiResponse;
import com.scoutingtcg.purchases.pokemoncard.dto.PokemonCardMarketPriceDto;
import com.scoutingtcg.purchases.pokemoncard.model.PokemonCardMarketPrice;
import com.scoutingtcg.purchases.pokemoncard.repository.PokemonCardMarketPriceRepository;
import com.scoutingtcg.purchases.cardforsale.repository.CardForSaleRepository;
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
        logger.info("Starting update for cards with no price and pending status...");

        Set<String> setIds = extractSetIds(cardForSaleRepository.findNoPriceAndPendingCardForSaleWithPokemonCard());
        logger.debug("Extracted set IDs: {}", setIds);

        List<PokemonCardMarketPriceDto> prices = getMarketPricesBySetIds(setIds);
        logger.debug("Fetched market prices: {}", prices);

        savePricesToDatabase(prices);
        logger.info("Saved market prices to the database.");

        List<CardForSale> cardForSales = cardForSaleRepository.findByPriceGreaterThanZeroAndStatusPending();
        logger.debug("Fetched cards for sale with price greater than zero and pending status: {}", cardForSales);

        updateCardForSaleWithLatestPrices(cardForSales);
        logger.info("Updated card prices for pending cards successfully.");
    }

    public void setCardForSalePrices(List<CardForSale> cardForSales) {
        logger.info("Setting prices for {} cards for sale.", cardForSales.size());
        updateCardForSaleWithLatestPrices(cardForSales);
        logger.info("Prices for cards for sale have been updated successfully.");
    }

    public void updateMarketPrices() {
        logger.info("Starting market prices update...");
        try {
            Set<String> setIds = extractSetIds(cardForSaleRepository.findAllCardForSaleWithPokemonCard());
            logger.debug("Extracted set IDs: {}", setIds);

            List<PokemonCardMarketPriceDto> prices = getMarketPricesBySetIds(setIds);
            logger.debug("Fetched market prices: {}", prices);

            savePricesToDatabase(prices);
            logger.info("Market prices update completed successfully.");
        } catch (Exception e) {
            logger.error("Failed to update market prices", e);
            throw new RuntimeException("Failed to update market prices", e);
        }
    }

    private List<PokemonCardMarketPriceDto> getMarketPricesBySetIds(Set<String> setIds) {
        logger.info("Fetching market prices for set IDs: {}", setIds);
        List<PokemonCardMarketPriceDto> allPrices = new ArrayList<>();

        int page = 1;
        int totalPages;

        do {
            String url = buildApiUrl(setIds, page);
            logger.debug("Fetching data from URL: {}", url);
            ResponseEntity<PokemonCardApiResponse> response = fetchApiResponse(url);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Failed to fetch Pokémon TCG data. Status: {}", response.getStatusCode());
                throw new IllegalStateException("Failed to fetch Pokémon TCG data. Status: " + response.getStatusCode());
            }
            PokemonCardApiResponse body = Objects.requireNonNull(response.getBody(), "Pokémon TCG API response is null.");
            logger.debug("Received response for page {}: {}", page, body);

            List<PokemonCardMarketPriceDto> pagePrices = extractPricesFromResponse(body);
            logger.debug("Extracted prices from response: {}", pagePrices);
            allPrices.addAll(pagePrices);

            totalPages = (int) Math.ceil((double) body.totalCount() / PAGE_SIZE);
            logger.debug("Total pages: {}, Current page: {}", totalPages, page);
            page++;

        } while (page <= totalPages);

        logger.info("Successfully fetched market prices for all pages.");
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
        return response.data().stream()
                .map(pokemonCardRaw -> {
                    Map<String, Double> priceMap = new HashMap<>();

                    if (pokemonCardRaw.tcgplayer() != null && pokemonCardRaw.tcgplayer().prices() != null) {
                        pokemonCardRaw.tcgplayer().prices().forEach((type, entry) -> {
                            if (entry.market() != null) {
                                priceMap.put(type, entry.market());
                            }
                        });
                    } else {
                        ensureEmptyPriceEntryExists(pokemonCardRaw.id());
                    }

                    return new PokemonCardMarketPriceDto(pokemonCardRaw.id(), priceMap);
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
        logger.info("Starting update for {} cards for sale.", cardForSales.size());

        List<PokemonCardMarketPrice> cardMarketPricesStored = priceRepository.findAllById(
                cardForSales.stream()
                        .map(CardForSale::getCardId)
                        .collect(Collectors.toSet())
        );
        logger.debug("Fetched {} market prices from the database.", cardMarketPricesStored.size());

        Map<String, Map<String, Double>> priceMapByCardId = cardMarketPricesStored.stream()
                .collect(Collectors.toMap(PokemonCardMarketPrice::getCardId, PokemonCardMarketPrice::getPrices));
        logger.debug("Constructed price map by card ID: {}", priceMapByCardId);

        for (CardForSale cardForSale : cardForSales) {
            String cardId = cardForSale.getCardId();
            logger.debug("Processing card for sale with ID: {}", cardId);

            if (priceMapByCardId.containsKey(cardId)) {
                Map<String, Double> pricesMap = priceMapByCardId.get(cardId);
                String cardPrinting = cardForSale.getPrinting();
                logger.debug("Card printing: {}, Prices map: {}", cardPrinting, pricesMap);

                if (pricesMap != null
                        && pricesMap.keySet().stream().anyMatch(
                        key -> key.contains(cardPrinting))) {
                    Double price = pricesMap.get(cardPrinting);
                    logger.debug("Found price: {} for card ID: {}", price, cardId);

                    if (price != null && price > 0) {
                        double updatedPrice = Math.floor(price * 0.95 * 100.0) / 100.0;
                        if (cardForSale.getPrice() < 10) {
                            logger.info("Updating price for card ID: {} from {} to {}", cardId, cardForSale.getPrice(), updatedPrice);
                            cardForSale.setPrice(updatedPrice);
                        } else {
                            if (cardForSale.getPrice() < updatedPrice) {
                                logger.info("Updating price for card ID: {} from {} to {}", cardId, cardForSale.getPrice(), updatedPrice);
                                cardForSale.setPrice(updatedPrice);
                            }
                        }
                    } else {
                        logger.debug("No price update needed for card ID: {}. Current price: {}, New price: {}", cardId, cardForSale.getPrice(), price);
                    }
                }
            } else {
                logger.warn("No market price found for card ID: {}", cardId);
            }
        }

        cardForSaleRepository.saveAll(cardForSales);
        logger.info("Successfully updated prices for {} cards for sale.", cardForSales.size());
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
