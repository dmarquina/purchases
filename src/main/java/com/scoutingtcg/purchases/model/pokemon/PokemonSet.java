package com.scoutingtcg.purchases.model.pokemon;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pokemon_sets")
@Data
public class PokemonSet {

    @Id
    private String id;

    private String name;
    private String series;
    private int printedTotal;
    private int total;
    private String ptcgoCode;
    private String imageSymbol;
    private String imageLogo;
    private LocalDate releaseDate;
    private LocalDateTime updatedAt;
}
