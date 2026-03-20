package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "contract_customers")
@Data
public class ContractCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private boolean isRepresentative;
}