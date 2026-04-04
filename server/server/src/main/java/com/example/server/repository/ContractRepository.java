package com.example.server.repository;

import com.example.server.entity.Contract;
import com.example.server.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByRoom_IdAndStatus(Long roomId, ContractStatus status);

}