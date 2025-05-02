package com.scoutingtcg.purchases.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pokemon_cards")
@Data
public class PokemonCard {

    @Id
    private String id;
    private String name;
    private String supertype;
    private String rarity;
    private String setName;
    private String setId;
    private String number;
    private String imageUrl;
}
