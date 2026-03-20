package com.example.server.services;

import com.example.server.entity.BuildingService;

import java.util.List;

public interface BuildingServiceService {

    List<BuildingService> getByBuilding(Long buildingId);

    BuildingService add(BuildingService entity);

    void delete(Long id);

}