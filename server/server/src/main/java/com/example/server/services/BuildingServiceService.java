package com.example.server.services;

import com.example.server.dto.BuildingServiceDTO;
import com.example.server.entity.BuildingService;

import java.util.List;

public interface BuildingServiceService {

    List<BuildingServiceDTO> getByBuilding(Long buildingId);

    BuildingService add(BuildingService entity);

    void delete(Long id);

}