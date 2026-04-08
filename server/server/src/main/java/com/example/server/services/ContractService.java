package com.example.server.services;

import com.example.server.dto.CreateContractRequest;
import com.example.server.entity.Contract;

import java.time.LocalDate;
import java.util.List;

public interface ContractService {

    void createContract(CreateContractRequest request);
    List<Contract> getExpiringContracts();
    void renewContract(Long contractId, LocalDate newEndDate);
}