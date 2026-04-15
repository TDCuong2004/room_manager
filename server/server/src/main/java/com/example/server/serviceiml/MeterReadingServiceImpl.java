package com.example.server.serviceiml;

import com.example.server.dto.MeterReadingViewDTO;
import com.example.server.entity.MeterReading;
import com.example.server.entity.Rooms;
import com.example.server.entity.ServiceEntity;
import com.example.server.enums.InvoiceStatus;
import com.example.server.enums.RoomStatus;
import com.example.server.repository.*;
import com.example.server.services.MeterReadingService;
import org.springframework.stereotype.Service;
import com.example.server.dto.MeterDTO;
import java.util.List;

@Service
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository repo;
    private final RoomRepository roomRepository;
    private final ServiceRepository serviceRepository;
    private final ContractCustomerRepository contractCustomerRepository;
    private final InvoiceRepository invoiceRepository;
    public MeterReadingServiceImpl(
            MeterReadingRepository repo,
            RoomRepository roomRepository,
            ServiceRepository serviceRepository,
            ContractCustomerRepository contractCustomerRepository,
            InvoiceRepository invoiceRepository // ✅ thêm vào đây
    ) {
        this.repo = repo;
        this.roomRepository = roomRepository;
        this.serviceRepository = serviceRepository;
        this.contractCustomerRepository = contractCustomerRepository;
        this.invoiceRepository = invoiceRepository; // ✅ gán
    }

    // ================= SAVE =================
    @Override
    public void saveAll(List<MeterDTO> list) {

        List<MeterReading> entities = list.stream().map(dto -> {

            Rooms room = roomRepository.findById(dto.roomId).orElseThrow();
            ServiceEntity service = serviceRepository.findById(dto.serviceId).orElseThrow();

            // 🚫 CHECK: hóa đơn đã PAID chưa
            boolean isPaid = invoiceRepository
                    .findByRoom_IdAndMonth(room.getId(), dto.month)
                    .map(inv -> inv.getStatus() == InvoiceStatus.PAID)
                    .orElse(false);

            if (isPaid) {
                throw new RuntimeException(
                        "Tháng " + dto.month + " phòng " + room.getRoomName() + " đã thanh toán, không thể sửa!"
                );
            }

            String type = service.getCalculationType().name();

            if (!type.equals("BY_METER")) return null;

            MeterReading existing = repo
                    .findByRoomAndServiceAndMonth(room, service, dto.month)
                    .orElse(null);

            if (existing != null) {
                existing.setNewValue(dto.newValue != null ? dto.newValue : 0.0);
                return existing;
            }

            MeterReading m = new MeterReading();
            m.setRoom(room);
            m.setService(service);
            m.setMonth(dto.month);

            MeterReading last = repo
                    .findTopByRoomAndServiceAndMonthLessThanOrderByMonthDesc(
                            room, service, dto.month
                    )
                    .orElse(null);

            m.setOldValue(last != null ? last.getNewValue() : 0.0);
            m.setNewValue(dto.newValue != null ? dto.newValue : 0.0);

            return m;

        }).filter(e -> e != null).toList();

        repo.saveAll(entities);
    }

    // ================= GET LAST =================
    @Override
    public MeterReading getLast(Long roomId, Long serviceId) {

        Rooms room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        return repo
                .findTopByRoomAndServiceOrderByMonthDesc(room, service)
                .orElse(null);
    }

    @Override
    public List<MeterReadingViewDTO> getAll(String month, Long serviceId, Long buildingId) {

        List<Rooms> rooms = roomRepository
                .findByBuildingIdAndStatus(buildingId, RoomStatus.RENTED);

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.getCalculationType().name().equals("BY_METER")) {
            return List.of();
        }

        return rooms.stream().map(room -> {

            MeterReading current = repo
                    .findByRoomAndServiceAndMonth(room, service, month)
                    .orElse(null);

            MeterReading last = repo
                    .findTopByRoomAndServiceAndMonthLessThanOrderByMonthDesc(
                            room, service, month
                    )
                    .orElse(null);

            // 🔥 CHECK PAID
            boolean isPaid = invoiceRepository
                    .findByRoom_IdAndMonth(room.getId(), month)
                    .map(inv -> inv.getStatus() == InvoiceStatus.PAID)
                    .orElse(false);

            MeterReadingViewDTO dto = new MeterReadingViewDTO();

            dto.setRoomId(room.getId());
            dto.setRoomName(room.getRoomName());

            String customerName = contractCustomerRepository
                    .findRepresentativeName(room.getId())
                    .orElse("Chưa có");

            dto.setCustomerName(customerName);

            dto.setServiceId(service.getId());
            dto.setServiceName(service.getServiceName());

            dto.setOldValue(last != null ? last.getNewValue() : 0.0);
            dto.setNewValue(current != null ? current.getNewValue() : null);

            dto.setPaid(isPaid); // ✅ QUAN TRỌNG

            return dto;

        }).toList();
    }
}