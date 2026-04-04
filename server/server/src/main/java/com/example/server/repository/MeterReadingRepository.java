package com.example.server.repository;
import com.example.server.entity.MeterReading;
import com.example.server.entity.Rooms;
import com.example.server.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

    Optional<MeterReading> findTopByRoomAndServiceOrderByMonthDesc(
            Rooms room,
            ServiceEntity service
    );
    Optional<MeterReading> findByRoomAndServiceAndMonth(
            Rooms room,
            ServiceEntity service,
            String month
    );
    boolean existsByRoomAndServiceAndMonth(
            Rooms room,
            ServiceEntity service,
            String month
    );
}