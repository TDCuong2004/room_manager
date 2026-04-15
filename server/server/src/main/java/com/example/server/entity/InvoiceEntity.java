package com.example.server.entity;

import com.example.server.enums.InvoiceStatus;
import com.example.server.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String month;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Rooms room;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}