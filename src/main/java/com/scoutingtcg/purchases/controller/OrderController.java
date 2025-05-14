package com.scoutingtcg.purchases.controller;

import com.scoutingtcg.purchases.dto.CartItemDto;
import com.scoutingtcg.purchases.dto.OrderDetailResponse;
import com.scoutingtcg.purchases.dto.OrderRequest;
import com.scoutingtcg.purchases.dto.PageResponse;
import com.scoutingtcg.purchases.model.OrderResponse;
import com.scoutingtcg.purchases.model.OrderStatus;
import com.scoutingtcg.purchases.model.OrderSummaryResponse;
import com.scoutingtcg.purchases.service.OrderService;
import com.scoutingtcg.purchases.util.PageUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @PostMapping(value = "/upload-payment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadPayment(@RequestParam("orderId") Long orderId,
                              @RequestPart("file") MultipartFile file) {
        orderService.uploadPayment(orderId, file);
    }

    @GetMapping("/summary")
    public PageResponse<OrderSummaryResponse> getAllOrderSummaries(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageUtils.toPageResponse(orderService.getAllOrderSummaries(pageable));
    }

    @PatchMapping("/{orderId}/status")
    public void updateStatus(@PathVariable Long orderId, @RequestBody OrderStatus status) {
        orderService.updateOrderStatus(orderId, status);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    @GetMapping("/users/{userId}")
    public PageResponse<OrderSummaryResponse> getUserOrderDetail(@PathVariable Long userId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageUtils.toPageResponse(orderService.getAllUserOrderSummaries(pageable, userId));
    }

}