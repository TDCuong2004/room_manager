package com.example.server.repository;

import com.example.server.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Rooms, Long> {

    List<Rooms> findByBuildingId(Long buildingId);

}