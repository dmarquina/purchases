package com.scoutingtcg.purchases.security.dto;

public record UserAddressDto(
        Long id,
        String recipientName,
        String addressLine,
        String city,
        String state,
        String zip,
        String country,
        boolean isDefault
) {
}
