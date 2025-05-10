package com.scoutingtcg.purchases.model;

import ch.qos.logback.core.status.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards_for_sale")
@Data
public class CardForSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Franchise franchise; // POKEMON, ONE_PIECE...

    private String cardId;
    private String printing; // Normal, Holo, Reverse Holo
    private String cardCondition; // Near Mint, Lightly Played, etc.
    private Double price;
    private Integer stock;
    @Enumerated(EnumType.STRING)
    private CardForSaleStatus status; // ACTIVE, INACTIVE, PENDING

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
