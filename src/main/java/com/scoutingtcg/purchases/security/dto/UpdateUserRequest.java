package com.scoutingtcg.purchases.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private Long userId;
    private String name;
    private String lastName;
    private String phone;
}
