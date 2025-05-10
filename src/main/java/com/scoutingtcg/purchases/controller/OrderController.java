package com.scoutingtcg.purchases.controller;

import com.scoutingtcg.purchases.dto.OrderRequest;
import com.scoutingtcg.purchases.model.Order;
import com.scoutingtcg.purchases.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order created = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(created);
    }
}
