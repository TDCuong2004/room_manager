package com.example.server.dto;

import com.example.server.entity.InvoiceEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaidInvoiceDTO {

    private String title;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String month;

    public PaidInvoiceDTO(InvoiceEntity inv) {
        this.amount = inv.getTotalAmount();
        this.paidAt = inv.getPaidAt();
        this.month = inv.getMonth();

        this.title = "Thanh toán tháng " + inv.getMonth();
    }
}
