package com.scoutingtcg.purchases.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Table(name = "purchases")
@Entity
@Data
public class Purchase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long purchaseId;

  @ManyToOne
  @JoinColumn(name = "supplier_id", nullable = false)
  private Supplier supplier;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  private LocalDate date;
  private String status;
  private Double deposit;
  private Double totalCost;
  private Integer quantity;
}