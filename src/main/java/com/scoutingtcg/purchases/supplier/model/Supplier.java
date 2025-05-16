package com.scoutingtcg.purchases.supplier.model;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "suppliers")
@Entity
@Data
public class Supplier
{

  @Id
  @GeneratedValue( strategy = GenerationType.IDENTITY )
  private Long supplierId;

  private String companyName;
  private String contactName;
  private String phone;
  private String email;
  private String address;
}
