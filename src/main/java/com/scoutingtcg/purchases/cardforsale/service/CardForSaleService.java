package com.scoutingtcg.purchases.cardforsale.service;

import com.scoutingtcg.purchases.cardforsale.dto.CardForSaleWithPokemonCardDto;
import com.scoutingtcg.purchases.cardforsale.model.CardForSale;
import com.scoutingtcg.purchases.cardforsale.repository.CardForSaleRepository;
import com.scoutingtcg.purchases.pokemoncard.dto.response.PokemonFilterOptionsResponse;
import com.scoutingtcg.purchases.pokemoncard.dto.response.PokemonSingleResponse;
import com.scoutingtcg.purchases.pokemoncard.dto.response.PokemonSingleVariantResponse;
import com.scoutingtcg.purchases.pokemoncard.dto.request.PokemonSinglesFilterRequest;
import com.scoutingtcg.purchases.pokemoncard.model.PokemonCard;
import com.scoutingtcg.purchases.pokemoncard.repository.PokemonCardRepository;
import com.scoutingtcg.purchases.pokemoncard.service.PokemonCardPriceService;
import com.scoutingtcg.purchases.shared.dto.PageResponse;
import com.scoutingtcg.purchases.shared.model.Franchise;
import com.scoutingtcg.purchases.shared.model.SetOption;
import com.scoutingtcg.purchases.shared.model.Status;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardForSaleService {

    private final CardForSaleRepository cardForSaleRepository;
    private final PokemonCardRepository pokemonCardRepository;
    private final PokemonCardPriceService pokemonCardPriceService;

    public CardForSaleService(CardForSaleRepository cardForSaleRepository, PokemonCardRepository pokemonCardRepository, PokemonCardPriceService pokemonCardPriceService) {
        this.cardForSaleRepository = cardForSaleRepository;
        this.pokemonCardRepository = pokemonCardRepository;
        this.pokemonCardPriceService = pokemonCardPriceService;
    }

    /**
     * Processes a CSV file containing card information and updates the database accordingly.
     *
     * @param is InputStream of the CSV file.
     * @return A list of rows that failed to process.
     * @throws IOException If an error occurs while reading the CSV file.
     */
    public List<CSVRecord> processCsv(InputStream is) throws IOException {
        List<CSVRecord> failedRows = new ArrayList<>();

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)
        ) {
            List<CardForSale> cardForSales = new ArrayList<>();
            for (CSVRecord row : parser) {
                try {
                    cardForSales.add(rowToCardForSale(row));
                } catch (Exception e) {
                    System.err.printf("Error processing row %d: %s%n", row.getRecordNumber(), e.getMessage());
                    failedRows.add(row);
                }
            }
            pokemonCardPriceService.setCardForSalePrices(cardForSales);
        }

        return failedRows;
    }


    /**
     * Generates a CSV file containing the rows that failed to process.
     *
     * @param failedRows List of CSVRecord objects representing the failed rows.
     * @return A byte array representing the CSV file.
     * @throws IOException If an error occurs while writing the CSV file.
     */
    public byte[] generateErrorCsv(List<CSVRecord> failedRows) throws IOException {
        String[] headers = {"Name", "Set Code", "Card Number", "Condition", "Printing", "Quantity"};

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))
        ) {
            for (CSVRecord row : failedRows) {
                printer.printRecord(
                        row.get("Name"),
                        row.get("Set Code"),
                        row.get("Card Number"),
                        row.get("Condition"),
                        row.get("Printing"),
                        row.get("Quantity")
                );
            }
            printer.flush();
            return out.toByteArray();
        }
    }

    /**
     * Retrieves a paginated list of Pokémon cards for sale based on the provided filters.
     *
     * @param filters  The filters to apply to the search.
     * @param pageable The pagination information.
     * @return A response object containing the filtered Pokémon cards.
     */
    public PageResponse<PokemonSingleResponse> getPokemonSingles(PokemonSinglesFilterRequest filters, Pageable pageable) {
        Page<String> pagedCardIds = cardForSaleRepository.findFilteredCardIdsWithPagination(
                filters.sets().isEmpty() ? null : filters.sets(),
                filters.conditions().isEmpty() ? null : filters.conditions(),
                filters.printings().isEmpty() ? null : filters.printings(),
                filters.name() == null || filters.name().isBlank() ? null : filters.name(),
                pageable
        );

        List<String> cardIds = pagedCardIds.getContent();
        List<CardForSaleWithPokemonCardDto> cfsWPokemonCardDto = cardForSaleRepository.findByCardIdIn(cardIds);
        List<PokemonSingleResponse> responses = getPokemonSingleResponses(cfsWPokemonCardDto);

        return new PageResponse<>(
                responses,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pagedCardIds.getTotalElements(),
                pagedCardIds.getTotalPages(),
                pagedCardIds.isLast()
        );
    }

    /**
     * Retrieves filter options for Pokémon cards.
     *
     * @return A response object containing filter options.
     */
    public PokemonFilterOptionsResponse getFilterOptions() {
        List<CardForSaleWithPokemonCardDto> data = cardForSaleRepository.findAllCardForSaleWithPokemonCard();

        Set<SetOption> sets = new HashSet<>();
        Set<String> conditions = new HashSet<>();
        Set<String> printings = new HashSet<>();

        for (CardForSaleWithPokemonCardDto dto : data) {
            PokemonCard card = dto.getPokemonCard();
            sets.add(new SetOption(card.getSetId(), card.getSetName()));
            conditions.add(dto.getCardForSale().getCardCondition());
            printings.add(dto.getCardForSale().getPrinting());
        }

        return new PokemonFilterOptionsResponse(
                new ArrayList<>(sets),
                new ArrayList<>(conditions),
                new ArrayList<>(printings)
        );
    }

    private CardForSale rowToCardForSale(CSVRecord row) {
        String name = row.get("Name").trim();
        String setId = row.get("SetId").trim();
        String number = row.get("Card Number").trim().split("/")[0];
        String cardCondition = row.get("Condition").trim();
        String printing = row.get("Printing").trim();
        int quantity = Integer.parseInt(row.get("Quantity"));

        String pokemonCardId = setId + "-" + Integer.parseInt(number);

        PokemonCard card = pokemonCardRepository.findById(pokemonCardId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Card not found: %s [%s] #%s", name, setId, number)));

        Optional<CardForSale> existing = cardForSaleRepository
                .findByCardIdAndCardConditionAndPrintingAndFranchise(
                        card.getId(), cardCondition, printing, Franchise.POKEMON);

        if (existing.isPresent()) {
            CardForSale cfs = existing.get();
            cfs.setStock(cfs.getStock() + quantity);
            cfs.setStatus(Status.PENDING);
            return cardForSaleRepository.save(cfs);
        } else {
            CardForSale newCfs = new CardForSale();
            newCfs.setCardId(card.getId());
            newCfs.setFranchise(Franchise.POKEMON);
            newCfs.setCardCondition(cardCondition);
            newCfs.setPrinting(printing);
            newCfs.setStock(quantity);
            newCfs.setPrice(0.0);
            newCfs.setStatus(Status.PENDING);
            return cardForSaleRepository.save(newCfs);
        }
    }

    private static List<PokemonSingleResponse> getPokemonSingleResponses(List<CardForSaleWithPokemonCardDto> cfsWPokemonCardDto) {
        Map<String, List<CardForSaleWithPokemonCardDto>> dtoGroupedByCfsId = cfsWPokemonCardDto.stream()
                .collect(Collectors.groupingBy(dto -> dto.getCardForSale().getCardId()));

        return dtoGroupedByCfsId.values().stream()
                .map(dtoGrouped -> {
                    CardForSaleWithPokemonCardDto base = dtoGrouped.get(0);
                    PokemonCard card = base.getPokemonCard();

                    List<PokemonSingleVariantResponse> variants = dtoGrouped.stream()
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
