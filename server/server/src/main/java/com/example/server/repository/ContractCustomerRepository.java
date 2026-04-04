package com.example.server.repository;

import com.example.server.entity.ContractCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContractCustomerRepository extends JpaRepository<ContractCustomer, Long> {
    @Query("""
    SELECT COUNT(cc.id)
    FROM ContractCustomer cc
    JOIN cc.contract c
    WHERE c.room.id = :roomId
    AND c.status = 'ACTIVE'
""")
    int countCurrentPeople(Long roomId);
    List<ContractCustomer> findByContractId(Long contractId);
}