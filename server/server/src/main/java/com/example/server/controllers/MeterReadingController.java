package com.example.server.controllers;

import com.example.server.dto.MeterDTO;
import com.example.server.dto.MeterReadingRequest;
import com.example.server.entity.MeterReading;
import com.example.server.services.MeterReadingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meter-readings")
@CrossOrigin
public class MeterReadingController {

    private final MeterReadingService service;

    public MeterReadingController(MeterReadingService service) {
        this.service = service;
    }

    // ✅ Lưu chỉ số
    @PostMapping("/bulk")
    public void save(@RequestBody List<MeterDTO> list){
        service.saveAll(list);
    }

    // ✅ Lấy chỉ số gần nhất (auto fill)
    @GetMapping("/last")
    public MeterReading getLast(
            @RequestParam Long roomId,
            @RequestParam Long serviceId
    ) {
        return service.getLast(roomId, serviceId);
    }
}