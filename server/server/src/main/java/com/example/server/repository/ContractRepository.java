package com.example.server.repository;

import com.example.server.entity.Contract;
import com.example.server.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByRoom_IdAndStatus(Long roomId, ContractStatus status);
    @Query("""
SELECT DISTINCT c FROM Contract c
LEFT JOIN FETCH c.contractCustomers cc
LEFT JOIN FETCH cc.customer
WHERE c.status = 'ACTIVE'
AND c.renewed = false
AND c.endDate BETWEEN :today AND :targetDate
""")
    List<Contract> findExpiringContracts(
            @Param("today") LocalDate today,
            @Param("targetDate") LocalDate targetDate
    );
    List<Contract> findByStatusAndStartDate(ContractStatus status, LocalDate startDate);
    Optional<Contract> findTopByRoom_IdOrderByEndDateDesc(Long roomId);
    List<Contract> findByStatusAndEndDateBefore(ContractStatus status, LocalDate date);
    boolean existsByRoomIdAndStatus(Long roomId, ContractStatus status);
}