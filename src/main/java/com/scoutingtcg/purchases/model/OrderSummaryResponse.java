package com.scoutingtcg.purchases.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderSummaryResponse {

    private String id;
    private String fullName;
    private Double total;
    private String receiptUrl;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private Long totalItems;

}
