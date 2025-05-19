package com.scoutingtcg.purchases.security.dto;

public record CreateOrUpdateUserAddressRequest(
        Long userId,
        String recipientName,
        String addressLine,
        String city,
        String state,
        String zip,
        String country,
        boolean setAsDefault
) {
}