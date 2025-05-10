package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}