package com.example.server.entity;

import com.example.server.enums.CalculationType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "services")
@Data
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceCode;
    private String serviceName;
    private String unit;

    @Enumerated(EnumType.STRING)
    private CalculationType calculationType;
}