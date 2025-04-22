package com.scoutingtcg.purchases.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String role;
    //TODO: Manage address later when we have to develop the checkout
}
