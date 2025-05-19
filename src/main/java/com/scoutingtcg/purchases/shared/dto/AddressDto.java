package com.scoutingtcg.purchases.shared.dto;

public record AddressDto(
        String fullName,
        String addressLine,
        String apartment,
        String city,
        String state,
        String zip,
        String country

) {
}
