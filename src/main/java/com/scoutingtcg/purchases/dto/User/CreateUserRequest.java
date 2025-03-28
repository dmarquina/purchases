package com.scoutingtcg.purchases.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String name;
    private String lastName;
    private String email;
    private String password;
}
