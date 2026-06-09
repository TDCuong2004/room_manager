package com.example.server.entity;

import com.example.server.enums.UserRole;
import com.example.server.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(length = 255)
    private String avatar;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "bank_account")
    private String bankAccount;

    // ===== CCCD =====

    @Column(name = "cccd_number")
    private String cccdNumber;

    @Column(name = "cccd_front_image")
    private String cccdFrontImage;

    @Column(name = "cccd_back_image")
    private String cccdBackImage;

    @Column(name = "cccd_issue_date")
    private LocalDate cccdIssueDate;

    @Column(name = "cccd_issue_place")
    private String cccdIssuePlace;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
}