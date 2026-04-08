package com.example.server.serviceiml;

import com.example.server.dto.CreateContractRequest;
import com.example.server.dto.CustomerContractDTO;
import com.example.server.entity.*;
import com.example.server.enums.ContractStatus;
import com.example.server.enums.CustomerStatus;
import com.example.server.enums.Gender;
import com.example.server.enums.RoomStatus;
import com.example.server.repository.*;
import com.example.server.services.ContractService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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

        // ================= VALIDATE =================
        if (request.getCustomers() == null || request.getCustomers().isEmpty()) {
            throw new RuntimeException("Phải có ít nhất 1 người thuê");
        }

        // check 1 người đại diện
        long repCount = request.getCustomers()
                .stream()
                .filter(CustomerContractDTO::isRepresentative)
                .count();

        if (repCount != 1) {
            throw new RuntimeException("Phải có đúng 1 người đại diện");
        }

        // ================= ROOM =================
        Rooms room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (request.getCustomers().size() > room.getMaxPeople()) {
            throw new RuntimeException("Vượt quá số người tối đa");
        }

        // ================= CONTRACT =================
        Contract contract = new Contract();
        contract.setRoom(room);
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setDeposit(request.getDeposit());
        contract.setRentPrice(request.getRentPrice());
        contract.setStatus(ContractStatus.ACTIVE);

        contractRepository.save(contract);

        // ================= CUSTOMERS =================
        for (CustomerContractDTO c : request.getCustomers()) {

            Customer customer = new Customer();
            customer.setFullName(c.getFullName());
            customer.setPhone(c.getPhone());
            customer.setBirthYear(c.getBirthYear());

            // tránh lỗi null gender
            if (c.getGender() != null) {
                customer.setGender(Gender.valueOf(c.getGender()));
            }

            customer.setCccdFrontImage(c.getCccdFrontImage());
            customer.setCccdBackImage(c.getCccdBackImage());
            customer.setStatus(CustomerStatus.ACTIVE);

            customer = customerRepository.save(customer);

            // mapping contract_customer
            ContractCustomer cc = new ContractCustomer();
            cc.setContract(contract);
            cc.setCustomer(customer);
            cc.setRepresentative(c.isRepresentative());

            contractCustomerRepository.save(cc);
        }

        // ================= UPDATE ROOM =================
        room.setStatus(RoomStatus.RENTED);
        roomRepository.save(room);
    }
    @Override
    public List<Contract> getExpiringContracts() {
        LocalDate today = LocalDate.now();
        LocalDate target = today.plusDays(3);

        return contractRepository.findExpiringContracts(today, target);
    }

    @Override
    @Transactional
    public void renewContract(Long contractId, LocalDate newEndDate) {

        Contract old = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        if (old.getStatus() != ContractStatus.ACTIVE) {
            throw new RuntimeException("Chỉ gia hạn contract ACTIVE");
        }

        // tạo contract mới
        Contract newContract = new Contract();
        newContract.setRoom(old.getRoom());
        newContract.setStartDate(old.getEndDate().plusDays(1));
        newContract.setEndDate(newEndDate);
        newContract.setDeposit(old.getDeposit());
        newContract.setRentPrice(old.getRentPrice());
        newContract.setStatus(ContractStatus.ACTIVE);

        contractRepository.save(newContract);

        // copy danh sách người
        List<ContractCustomer> oldCustomers =
                contractCustomerRepository.findByContract_Id(old.getId());

        for (ContractCustomer cc : oldCustomers) {
            ContractCustomer newCC = new ContractCustomer();
            newCC.setContract(newContract);
            newCC.setCustomer(cc.getCustomer());
            newCC.setRepresentative(cc.isRepresentative());

            contractCustomerRepository.save(newCC);
        }
    }
}