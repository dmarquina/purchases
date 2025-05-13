package com.scoutingtcg.purchases.controller;

import com.scoutingtcg.purchases.dto.OrderRequest;
import com.scoutingtcg.purchases.dto.CartItemDto;
import com.scoutingtcg.purchases.model.Order;
import com.scoutingtcg.purchases.model.Product;
import com.scoutingtcg.purchases.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/check-stock")
    public ResponseEntity<List<CartItemDto>> checkStock(@RequestBody List<CartItemDto> cartItems) {
        return ResponseEntity.ok(orderService.checkStockAvailability(cartItems));
    }

    //TODO: dto para order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @PostMapping(value = "/upload-payment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadPayment(@RequestParam("orderId") Long orderId,
                              @RequestPart("file") MultipartFile file) {
        orderService.uploadPayment(orderId, file);
    }
}