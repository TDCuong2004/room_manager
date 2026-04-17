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

    private final MeterReadingRepository meterReadingRepository;
    private final BuildingServiceRepository buildingServiceRepository;
    private final ContractRepository contractRepository;
    private final ContractCustomerRepository contractCustomerRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;

    public ContractServiceImpl(
            ContractRepository contractRepository,
            ContractCustomerRepository contractCustomerRepository,
            RoomRepository roomRepository,
            CustomerRepository customerRepository,
            MeterReadingRepository meterReadingRepository,
            BuildingServiceRepository buildingServiceRepository
    ) {
        this.contractRepository = contractRepository;
        this.contractCustomerRepository = contractCustomerRepository;
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.buildingServiceRepository = buildingServiceRepository;
    }

    @Override
    @Transactional
    public void createContract(CreateContractRequest request) {

        // ================= VALIDATE =================
        if (request.getCustomers() == null || request.getCustomers().isEmpty()) {
            throw new RuntimeException("Phải có ít nhất 1 người thuê");
        }

        long repCount = request.getCustomers()
                .stream()
                .filter(CustomerContractDTO::isRepresentative)
                .count();

        if (repCount != 1) {
            throw new RuntimeException("Phải có đúng 1 người đại diện");
        }

        Rooms room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (request.getCustomers().size() > room.getMaxPeople()) {
            throw new RuntimeException("Vượt quá số người tối đa");
        }

        // ================= CHECK OVERLAP =================
        boolean overlap = contractRepository.findAll().stream()
                .anyMatch(c ->
                        c.getRoom().getId().equals(room.getId()) &&
                                !(request.getEndDate().isBefore(c.getStartDate()) ||
                                        request.getStartDate().isAfter(c.getEndDate()))
                );

        if (overlap) {
            throw new RuntimeException("Phòng đã có hợp đồng trong khoảng thời gian này");
        }

        // ================= CONTRACT =================
        Contract contract = new Contract();
        contract.setRoom(room);
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setDeposit(request.getDeposit());
        contract.setRentPrice(request.getRentPrice());

        LocalDate today = LocalDate.now();

        if (!today.isBefore(request.getStartDate())
                && !today.isAfter(request.getEndDate())) {

            contract.setStatus(ContractStatus.ACTIVE);

        } else if (today.isBefore(request.getStartDate())) {

            contract.setStatus(ContractStatus.PENDING);

        } else {

            contract.setStatus(ContractStatus.EXPIRED);
        }

        contractRepository.save(contract);

        // ================= INIT METER =================
        List<BuildingService> services =
                buildingServiceRepository.findByBuildingId(room.getBuilding().getId());

        String month = request.getStartDate().toString().substring(0, 7);

        for (BuildingService bs : services) {

            ServiceEntity service = bs.getService();

            // ✅ chỉ xử lý service cần nhập số
            if (!service.getCalculationType().name().equals("BY_METER")) continue;

            // ✅ check đã tồn tại chưa
            boolean exists = meterReadingRepository
                    .findByRoomAndServiceAndMonth(room, service, month)
                    .isPresent();

            if (exists) continue;

            MeterReading m = new MeterReading();
            m.setRoom(room);
            m.setService(service);
            m.setMonth(month);

            // ================= LẤY CHỈ SỐ TỪ FE =================
            Double oldValue = null;

            if (request.getMeterOldValues() != null) { oldValue = request.getMeterOldValues().get(service.getId()); }
            // fallback nếu không nhập
            m.setOldValue(oldValue != null ? oldValue : 0);
            m.setNewValue(null);

            meterReadingRepository.save(m);
        }

        // ================= CUSTOMERS =================
        for (CustomerContractDTO c : request.getCustomers()) {

            Customer customer = new Customer();
            customer.setFullName(c.getFullName());
            customer.setPhone(c.getPhone());
            customer.setBirthYear(c.getBirthYear());

            if (c.getGender() != null) {
                customer.setGender(Gender.valueOf(c.getGender()));
            }

            customer.setCccdFrontImage(c.getCccdFrontImage());
            customer.setCccdBackImage(c.getCccdBackImage());
            customer.setStatus(CustomerStatus.ACTIVE);

            customer = customerRepository.save(customer);

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

        LocalDate newStartDate = old.getEndDate().plusDays(1);

        boolean overlap = contractRepository.findAll().stream()
                .anyMatch(c ->
                        c.getRoom().getId().equals(old.getRoom().getId()) &&
                                !(newEndDate.isBefore(c.getStartDate()) ||
                                        newStartDate.isAfter(c.getEndDate()))
                );

        if (overlap) {
            throw new RuntimeException("Hợp đồng mới bị trùng thời gian");
        }

        Contract newContract = new Contract();
        newContract.setRoom(old.getRoom());
        newContract.setStartDate(newStartDate);
        newContract.setEndDate(newEndDate);
        newContract.setDeposit(old.getDeposit());
        newContract.setRentPrice(old.getRentPrice());

        LocalDate today = LocalDate.now();

        if (!today.isBefore(newStartDate)
                && !today.isAfter(newEndDate)) {

            newContract.setStatus(ContractStatus.ACTIVE);

        } else if (today.isBefore(newStartDate)) {

            newContract.setStatus(ContractStatus.PENDING);

        } else {

            newContract.setStatus(ContractStatus.EXPIRED);
        }

        contractRepository.save(newContract);
        old.setRenewed(true);
        contractRepository.save(old);

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