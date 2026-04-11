package com.example.server.services;

import com.example.server.dto.MeterDTO;
import com.example.server.dto.MeterReadingRequest;
import com.example.server.dto.MeterReadingViewDTO;
import com.example.server.entity.MeterReading;
import com.example.server.entity.Rooms;
import com.example.server.entity.ServiceEntity;

import java.util.List;
import java.util.Optional;

public interface MeterReadingService {

    void saveAll(List<MeterDTO> list);

    MeterReading getLast(Long roomId, Long serviceId);
    List<MeterReadingViewDTO> getAll(String month, Long serviceId, Long buildingId);
}