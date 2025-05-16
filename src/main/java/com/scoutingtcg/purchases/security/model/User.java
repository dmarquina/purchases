package com.scoutingtcg.purchases.security.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    private String phone;
    private String role;
}
