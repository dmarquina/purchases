package com.scoutingtcg.purchases.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggedUserResponse {
    private Long userId;
    private String name;
    private String lastName;
    private String userName;
    private String email;
}
