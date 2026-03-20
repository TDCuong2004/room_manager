package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "buildings")
@Data
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buildingCode;
    private String buildingName;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}