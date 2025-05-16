package com.scoutingtcg.purchases.security.dto;

public record LoggedUserResponse(
        Long userId,
        String name,
        String lastName,
        String email,
        String phone,
        String role,
        String token
) {
}
