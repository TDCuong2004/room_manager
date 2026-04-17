package com.example.server.controllers;

import com.example.server.dto.ContractCustomerResponse;
import com.example.server.dto.ContractDTO;
import com.example.server.dto.CreateContractRequest;
import com.example.server.entity.Contract;
import com.example.server.entity.ContractCustomer;
import com.example.server.enums.ContractStatus;
import com.example.server.repository.ContractRepository;
import com.example.server.services.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin
public class ContractController {

    private final ContractRepository contractRepository;
    private final ContractService contractService;
    public ContractController(
            ContractService contractService,
            ContractRepository contractRepository
    ) {
        this.contractService = contractService;
        this.contractRepository = contractRepository;
    }

    // ================= CREATE CONTRACT =================
    @PostMapping
    public ResponseEntity<?> createContract(@RequestBody CreateContractRequest request) {

        contractService.createContract(request);

        return ResponseEntity.ok("Contract created");
    }

    // ================= GET CONTRACT SẮP HẾT HẠN =================
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<ContractDTO>> getExpiringContracts() {

        List<ContractDTO> list = contractService.getExpiringContracts()
                .stream()
                .map(ContractDTO::new)
                .toList();

        return ResponseEntity.ok(list);
    }

    // ================= GIA HẠN CONTRACT =================
    @PostMapping("/{id}/renew")
    public ResponseEntity<?> renewContract(
            @PathVariable Long id,
            @RequestParam String endDate
    ) {

        contractService.renewContract(id, LocalDate.parse(endDate));

        return ResponseEntity.ok("Renew success");
    }
    @GetMapping("/room/{roomId}/tenant")
    public ContractCustomerResponse getTenant(@PathVariable Long roomId) {

        Contract contract = contractRepository
                .findByRoom_IdAndStatus(roomId, ContractStatus.ACTIVE)
                .orElse(null);

        if (contract == null) return null;

        // 👉 lấy người đại diện
        ContractCustomer cc = contract.getContractCustomers()
                .stream()
                .filter(ContractCustomer::isRepresentative)
                .findFirst()
                .orElse(null);

        if (cc == null) return null;

        return new ContractCustomerResponse(cc);
    }
    @GetMapping("/room/{roomId}")
    public ResponseEntity<?> getContractByRoom(@PathVariable Long roomId) {

        Contract contract = contractRepository
                .findByRoom_IdAndStatus(roomId, ContractStatus.ACTIVE)
                .orElse(null);

        if (contract == null) return ResponseEntity.ok(null);

        return ResponseEntity.ok(new ContractDTO(contract));
    }
}