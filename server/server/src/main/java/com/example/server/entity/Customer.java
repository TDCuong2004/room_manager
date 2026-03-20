package com.example.server.entity;

import com.example.server.enums.CustomerStatus;
import com.example.server.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private Integer birthYear;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate moveInDate;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    private String cccdFrontImage;
    private String cccdBackImage;
}