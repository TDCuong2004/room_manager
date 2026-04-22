package com.example.server.entity;

import com.example.server.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String fullName;
    private String phone;
    private String email;

    @Column(length = 255)
    private String avatar;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "bank_account")
    private String bankAccount;
}