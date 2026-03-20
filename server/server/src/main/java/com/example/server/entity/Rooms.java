package com.example.server.entity;

import com.example.server.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Data
public class Rooms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomCode;
    private String roomName;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private BigDecimal price;
    private BigDecimal area;
    private Integer maxPeople;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;
}