package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "meter_readings")
@Data
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Rooms room;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceEntity  service;

    private String month;
    private Double oldValue;
    private Double newValue;
}