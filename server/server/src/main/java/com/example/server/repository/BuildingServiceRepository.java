package com.example.server.repository;

import com.example.server.dto.RoomServiceDTO;
import com.example.server.entity.BuildingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BuildingServiceRepository extends JpaRepository<BuildingService, Long> {

    List<RoomServiceDTO> getServicesByBuilding(Long buildingId);
    List<BuildingService> findByBuildingId(Long buildingId);
}