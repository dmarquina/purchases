package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}