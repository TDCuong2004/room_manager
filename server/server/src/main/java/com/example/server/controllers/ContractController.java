package com.example.server.controllers;

import com.example.server.dto.CreateContractRequest;
import com.example.server.services.ContractService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping
    public String createContract(@RequestBody CreateContractRequest request){

        contractService.createContract(request);

        return "Contract created";

    }

}