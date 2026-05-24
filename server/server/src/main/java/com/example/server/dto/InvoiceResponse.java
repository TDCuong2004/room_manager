package com.example.server.dto;

import com.example.server.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {

    private Long id;

    private String roomName;

    private String month;

    private BigDecimal totalAmount;

    // ✅ THÊM TIỀN PHÒNG
    private BigDecimal rentPrice;

    private InvoiceStatus status;

    private String bankCode;

    private String bankAccount;
}