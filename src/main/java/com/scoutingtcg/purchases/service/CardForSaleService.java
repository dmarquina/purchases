package com.scoutingtcg.purchases.service;

import com.scoutingtcg.purchases.dto.CardForSale.CardForSaleWithPokemonCardDto;
import com.scoutingtcg.purchases.dto.CardForSale.PokemonSingleResponse;
import com.scoutingtcg.purchases.dto.CardForSale.PokemonSingleVariantResponse;
import com.scoutingtcg.purchases.model.CardForSale;
import com.scoutingtcg.purchases.model.Franchise;
import com.scoutingtcg.purchases.model.PokemonCard;
import com.scoutingtcg.purchases.repository.CardForSaleRepository;
import com.scoutingtcg.purchases.repository.PokemonCardRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardForSaleService {

    private final CardForSaleRepository cardForSaleRepository;
    private final PokemonCardRepository pokemonCardRepository;

    public CardForSaleService(CardForSaleRepository cardForSaleRepository, PokemonCardRepository pokemonCardRepository) {
        this.cardForSaleRepository = cardForSaleRepository;
        this.pokemonCardRepository = pokemonCardRepository;
    }

    public List<CSVRecord> processCsv(InputStream is) throws IOException {
        List<CSVRecord> failedRows = new ArrayList<>();

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)
        ) {
            for (CSVRecord row : parser) {
                try {
                    processRow(row);
                } catch (Exception e) {
                    System.err.printf("Error processing row %d: %s%n", row.getRecordNumber(), e.getMessage());
                    failedRows.add(row);
                }
            }
        }

        return failedRows;
    }

    private void processRow(CSVRecord row) {
        String name = row.get("Name").trim();
        String rawSet = row.get("Set").trim();
        String rawSetId = rawSet.split(":")[0].toLowerCase().trim();
        String setId = rawSetId.replaceAll("(?<=\\D)0+(?=\\d)", "");
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
            cardForSaleRepository.save(cfs);
        } else {
            CardForSale newCfs = new CardForSale();
            newCfs.setCardId(card.getId());
            newCfs.setFranchise(Franchise.POKEMON);
            newCfs.setCardCondition(cardCondition);
            newCfs.setPrinting(printing);
            newCfs.setStock(quantity);
            newCfs.setPrice(0.0);
            cardForSaleRepository.save(newCfs);
        }
    }

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

    public Page<PokemonSingleResponse> getPokemonSingles(Pageable pageable) {
        List<CardForSaleWithPokemonCardDto> data = cardForSaleRepository.findWithPokemonCard(pageable);
        long total = cardForSaleRepository.countAllWithPokemonCard();

        Map<String, List<CardForSaleWithPokemonCardDto>> grouped = data.stream()
                .collect(Collectors.groupingBy(dto -> dto.getCardForSale().getCardId()));

        List<PokemonSingleResponse> responses = grouped.entrySet().stream()
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
                            .variants(variants)
                            .build();
                })
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }
}
