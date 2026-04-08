package com.example.server.repository;

import com.example.server.entity.Contract;
import com.example.server.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByRoom_IdAndStatus(Long roomId, ContractStatus status);
    @Query("""
    SELECT c FROM Contract c
    WHERE c.status = com.example.server.enums.ContractStatus.ACTIVE
    AND c.endDate BETWEEN :today AND :targetDate
""")
    List<Contract> findExpiringContracts(LocalDate today, LocalDate targetDate);
}