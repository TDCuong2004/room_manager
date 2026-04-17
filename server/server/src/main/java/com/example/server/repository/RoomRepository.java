package com.example.server.repository;

import com.example.server.entity.Rooms;
import com.example.server.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Rooms, Long> {

    List<Rooms> findByBuildingId(Long buildingId);
    List<Rooms> findByBuildingIdAndStatus(Long buildingId, RoomStatus status);
    boolean existsByRoomNameAndBuilding_Id(String roomName, Long buildingId);

}