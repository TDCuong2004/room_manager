package com.example.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class InvoiceDetailDTO {
    private Long id;
    private String serviceName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
}
