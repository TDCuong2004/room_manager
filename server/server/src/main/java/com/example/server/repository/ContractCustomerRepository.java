package com.example.server.repository;

import com.example.server.entity.ContractCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractCustomerRepository extends JpaRepository<ContractCustomer, Long> {
}