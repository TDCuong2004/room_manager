package com.example.server.services;

import com.example.server.dto.CreateContractRequest;
import com.example.server.dto.CustomerContractDTO;
import com.example.server.entity.*;
import com.example.server.enums.RoomStatus;
import com.example.server.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ContractCustomerRepository contractCustomerRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;

    public ContractServiceImpl(
            ContractRepository contractRepository,
            ContractCustomerRepository contractCustomerRepository,
            RoomRepository roomRepository,
            CustomerRepository customerRepository
    ) {
        this.contractRepository = contractRepository;
        this.contractCustomerRepository = contractCustomerRepository;
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public void createContract(CreateContractRequest request) {

        Rooms room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Contract contract = new Contract();
        contract.setRoom(room);
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setDeposit(request.getDeposit());
        contract.setRentPrice(request.getRentPrice());

        contractRepository.save(contract);

        for(CustomerContractDTO c : request.getCustomers()){

            Customer customer = customerRepository.findById(c.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            ContractCustomer cc = new ContractCustomer();
            cc.setContract(contract);
            cc.setCustomer(customer);
            cc.setRepresentative(c.isRepresentative());

            contractCustomerRepository.save(cc);

        }

        room.setStatus(RoomStatus.RENTED);
        roomRepository.save(room);

    }

}