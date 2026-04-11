package com.example.server.dto;

import lombok.Data;

@Data
public class MeterReadingViewDTO {
    private Long roomId;
    private String roomName;
    private String customerName;

    private Long serviceId;
    private String serviceName;

    private Double oldValue;
    private Double newValue;
}
