package com.scoutingtcg.purchases.pokemoncard.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "pokemon_card_market_prices")
@Data
@NoArgsConstructor
public class PokemonCardMarketPrice {

    @Id
    private String cardId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pokemon_card_market_price_entries", joinColumns = @JoinColumn(name = "card_id"))
    @MapKeyColumn(name = "card_type")
    @Column(name = "market_price")
    private Map<String, Double> prices;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

}
