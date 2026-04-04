package com.example.server.dto;

import java.math.BigDecimal;
import com.example.server.enums.CalculationType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomServiceDTO {

    private Long id;
    private String serviceName;
    private CalculationType calculationType;
    private BigDecimal price;
}