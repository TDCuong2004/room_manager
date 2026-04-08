package com.example.server.serviceiml;

import com.example.server.dto.MeterReadingViewDTO;
import com.example.server.entity.MeterReading;
import com.example.server.entity.Rooms;
import com.example.server.entity.ServiceEntity;
import com.example.server.enums.RoomStatus;
import com.example.server.repository.ContractCustomerRepository;
import com.example.server.repository.MeterReadingRepository;
import com.example.server.repository.RoomRepository;
import com.example.server.repository.ServiceRepository;
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

    public MeterReadingServiceImpl(MeterReadingRepository repo,
                                   RoomRepository roomRepository,
                                   ServiceRepository serviceRepository,
                                   ContractCustomerRepository contractCustomerRepository) {
        this.repo = repo;
        this.roomRepository = roomRepository;
        this.serviceRepository = serviceRepository;
        this.contractCustomerRepository = contractCustomerRepository;
    }

    // ================= SAVE =================
    @Override
    public void saveAll(List<MeterDTO> list) {

        List<MeterReading> entities = list.stream().map(dto -> {

            Rooms room = roomRepository.findById(dto.roomId).orElseThrow();
            ServiceEntity service = serviceRepository.findById(dto.serviceId).orElseThrow();

            // 🔥 check existing (KHÔNG throw nữa)
            MeterReading existing = repo
                    .findByRoomAndServiceAndMonth(room, service, dto.month)
                    .orElse(null);

            if (existing != null) {
                existing.setNewValue(dto.newValue);
                return existing;
            }

            MeterReading m = new MeterReading();
            m.setRoom(room);
            m.setService(service);
            m.setMonth(dto.month);

            // 🔥 oldValue auto
            MeterReading last = repo
                    .findTopByRoomAndServiceOrderByMonthDesc(room, service)
                    .orElse(null);

            m.setOldValue(last != null ? last.getNewValue() : 0);

            String type = service.getCalculationType().name();

            if (type.equals("BY_METER")) {
                m.setNewValue(dto.newValue != null ? dto.newValue : 0);
            } else {
                m.setNewValue(1); // FIXED / BY_PERSON (tạm)
            }

            return m;

        }).toList();

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

    // ================= GET ALL (🔥 CHO UI) =================
    @Override
    public List<MeterReadingViewDTO> getAll(String month, Long serviceId, Long buildingId) {
        try {

            List<Rooms> rooms = roomRepository
                    .findByBuildingIdAndStatus(buildingId, RoomStatus.RENTED);
            ServiceEntity service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            return rooms.stream().map(room -> {

                MeterReading current = repo
                        .findByRoomAndServiceAndMonth(room, service, month)
                        .orElse(null);

                MeterReading last = repo
                        .findTopByRoomAndServiceOrderByMonthDesc(room, service)
                        .orElse(null);

                MeterReadingViewDTO dto = new MeterReadingViewDTO();

                dto.setRoomId(room.getId());
                dto.setRoomName(room.getRoomName());

                String customerName = contractCustomerRepository
                        .findRepresentativeName(room.getId())
                        .orElse("Chưa có");

                dto.setCustomerName(customerName);

                dto.setServiceId(service.getId());
                dto.setServiceName(service.getServiceName());

                dto.setOldValue(last != null ? last.getNewValue() : 0);
                dto.setNewValue(current != null ? current.getNewValue() : null);

                return dto;

            }).toList();

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 in lỗi thật
            throw new RuntimeException("Lỗi load meter readings: " + e.getMessage());
        }
    }
}