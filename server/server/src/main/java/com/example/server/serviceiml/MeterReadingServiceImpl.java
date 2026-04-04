package com.example.server.serviceiml;

import com.example.server.entity.MeterReading;
import com.example.server.entity.Rooms;
import com.example.server.entity.ServiceEntity;
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

    // ✅ Constructor duy nhất
    public MeterReadingServiceImpl(MeterReadingRepository repo,
                                   RoomRepository roomRepository,
                                   ServiceRepository serviceRepository) {
        this.repo = repo;
        this.roomRepository = roomRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public void saveAll(List<MeterDTO> list) {

        List<MeterReading> entities = list.stream().map(dto -> {

            Rooms room = roomRepository.findById(dto.roomId).orElseThrow();
            ServiceEntity service = serviceRepository.findById(dto.serviceId).orElseThrow();

            // ❗ check trùng tháng
            if(repo.existsByRoomAndServiceAndMonth(room, service, dto.month)){
                throw new RuntimeException("Đã nhập tháng này rồi");
            }

            MeterReading m = new MeterReading();

            m.setRoom(room);
            m.setService(service);
            m.setMonth(dto.month);

            // 🔥 old value
            MeterReading last = repo
                    .findTopByRoomAndServiceOrderByMonthDesc(room, service)
                    .orElse(null);

            m.setOldValue(last != null ? last.getNewValue() : 0);

            String type = service.getCalculationType().name();

            // 🔥 tất cả đều dùng newValue
            if(type.equals("BY_METER")){
                m.setNewValue(dto.newValue != null ? dto.newValue : 0);
            }

            else { // FIXED
                m.setNewValue(1);
            }

            return m;

        }).toList();

        repo.saveAll(entities);
    }

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
}