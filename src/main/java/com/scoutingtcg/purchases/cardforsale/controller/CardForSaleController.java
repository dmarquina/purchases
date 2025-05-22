package com.scoutingtcg.purchases.cardforsale.controller;

import com.scoutingtcg.purchases.cardforsale.service.CardForSaleService;
import com.scoutingtcg.purchases.pokemoncard.dto.response.PokemonFilterOptionsResponse;
import com.scoutingtcg.purchases.pokemoncard.dto.response.PokemonSingleResponse;
import com.scoutingtcg.purchases.pokemoncard.dto.request.PokemonSinglesFilterRequest;
import com.scoutingtcg.purchases.shared.dto.PageResponse;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/card-for-sale")
public class CardForSaleController {

    private final CardForSaleService cardForSaleService;

    public CardForSaleController(CardForSaleService cardForSaleService) {
        this.cardForSaleService = cardForSaleService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pokemon/upload-cards")
    public ResponseEntity<?> uploadCards(@RequestParam("file") MultipartFile file) {
        try {
            List<CSVRecord> failedRows = cardForSaleService.processCsv(file.getInputStream());

            if (failedRows.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            byte[] errorCsv = cardForSaleService.generateErrorCsv(failedRows);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=failed_cards.csv")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(errorCsv);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process file: " + e.getMessage());
        }
    }

    @PostMapping("/pokemon")
    public PageResponse<PokemonSingleResponse> getPokemonSingles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestBody PokemonSinglesFilterRequest filters
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return cardForSaleService.getPokemonSingles(filters, pageable);
    }

    @GetMapping("/pokemon/filters")
    public PokemonFilterOptionsResponse getPokemonCardFilters() {
        return cardForSaleService.getFilterOptions();
    }
}
