package com.example.server.services;

import com.example.server.dto.MeterDTO;
import com.example.server.dto.MeterReadingRequest;
import com.example.server.entity.MeterReading;

import java.util.List;

public interface MeterReadingService {

    void saveAll(List<MeterDTO> list);

    MeterReading getLast(Long roomId, Long serviceId);
}