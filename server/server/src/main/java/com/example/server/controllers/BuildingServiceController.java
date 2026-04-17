package com.example.server.controllers;

import com.example.server.dto.BuildingServiceDTO;
import com.example.server.entity.BuildingService;
import com.example.server.services.BuildingServiceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/building-services")
@CrossOrigin
public class BuildingServiceController {

    private final BuildingServiceService service;

    public BuildingServiceController(BuildingServiceService service) {
        this.service = service;
    }

    @GetMapping("/{buildingId}")
    public List<BuildingServiceDTO> getByBuilding(@PathVariable Long buildingId) {
        return service.getByBuilding(buildingId);
    }
    @GetMapping("/{buildingId}/meter")
    public List<BuildingServiceDTO> getMeter(@PathVariable Long buildingId) {
        return service.getByBuildingMeter(buildingId);
    }
    @PostMapping
    public BuildingService add(@RequestBody BuildingService entity) {
        return service.add(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}