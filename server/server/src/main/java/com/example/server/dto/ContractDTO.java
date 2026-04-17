package com.example.server.dto;

import com.example.server.entity.Contract;
import com.example.server.entity.ContractCustomer;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ContractDTO {

    private Long id;
    private String roomName;
    private LocalDate endDate;
    private LocalDate startDate;
    private List<String> customers;

    public ContractDTO(Contract c) {
        this.id = c.getId();
        this.roomName = c.getRoom().getRoomName();
        this.startDate = c.getStartDate();
        this.endDate = c.getEndDate();

        this.customers = c.getContractCustomers()
                .stream()
                .filter(ContractCustomer::isRepresentative)
                .map(cc -> cc.getCustomer().getFullName())
                .toList();
    }
}
