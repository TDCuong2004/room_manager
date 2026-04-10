package com.example.server.entity;

import com.example.server.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "contracts")
@Data
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Rooms room;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal deposit;

    private BigDecimal rentPrice;
    private boolean renewed = false;
    @Enumerated(EnumType.STRING)
    private ContractStatus status;
    @OneToMany(mappedBy = "contract")
    private List<ContractCustomer> contractCustomers;
}