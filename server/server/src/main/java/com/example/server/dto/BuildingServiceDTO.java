package com.example.server.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BuildingServiceDTO {
    private Long id;
    private BigDecimal price;

    private Long serviceId;
    private String serviceName;
    private String calculationType;
}