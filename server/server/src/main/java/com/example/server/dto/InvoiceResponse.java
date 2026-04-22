package com.example.server.dto;

import com.example.server.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class InvoiceResponse {

    private Long id;
    private String roomName;
    private String month;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private String bankCode;
    private String bankAccount;
}