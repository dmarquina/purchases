package com.scoutingtcg.purchases.controller;

import com.scoutingtcg.purchases.dto.ShippingCalculateRequest;
import com.scoutingtcg.purchases.dto.ShippingSummary;
import com.scoutingtcg.purchases.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    @Autowired
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping("/calculate")
    public ShippingSummary calculateShippingCost(@RequestBody ShippingCalculateRequest request) {
        return shippingService.calculateShippingCost(request);
    }
}