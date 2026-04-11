package com.example.server.repository;

import com.example.server.dto.RoomServiceDTO;
import com.example.server.entity.BuildingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuildingServiceRepository extends JpaRepository<BuildingService, Long> {
    @Query("""
    SELECT new com.example.server.dto.RoomServiceDTO(
        s.id,
        s.serviceName,
        s.calculationType,
        bs.price
    )
    FROM BuildingService bs
    JOIN bs.service s
    WHERE bs.building.id = :buildingId
    """)
    List<RoomServiceDTO> getServicesByBuilding(@Param("buildingId") Long buildingId);
    List<BuildingService> findByBuildingId(Long buildingId);
}