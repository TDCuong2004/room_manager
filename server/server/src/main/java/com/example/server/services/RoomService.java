package com.example.server.services;

import com.example.server.entity.Rooms;

import java.util.List;

public interface RoomService {

    Rooms create(Rooms room);

    List<Rooms> getAll();

    Rooms getById(Long id);

    List<Rooms> getByBuilding(Long buildingId);

    Rooms update(Long id, Rooms room);

    void delete(Long id);
}