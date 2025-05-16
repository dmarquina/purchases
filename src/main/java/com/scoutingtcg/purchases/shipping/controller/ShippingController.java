package com.scoutingtcg.purchases.shipping.controller;

import com.scoutingtcg.purchases.shipping.service.ShippingService;
import com.scoutingtcg.purchases.shipping.dto.ShippingSummary;
import com.scoutingtcg.purchases.shipping.dto.ShippingCalculateRequest;
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