package com.scoutingtcg.purchases.dto.User;

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
