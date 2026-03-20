package com.example.server.services;

import com.example.server.entity.Rooms;
import com.example.server.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Rooms create(Rooms room) {
        return roomRepository.save(room);
    }

    @Override
    public List<Rooms> getAll() {
        return roomRepository.findAll();
    }

    @Override
    public Rooms getById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Override
    public List<Rooms> getByBuilding(Long buildingId) {
        return roomRepository.findByBuildingId(buildingId);
    }

    @Override
    public Rooms update(Long id, Rooms roomRequest) {
        Rooms room = getById(id);

        room.setRoomCode(roomRequest.getRoomCode());
        room.setRoomName(roomRequest.getRoomName());
        room.setPrice(roomRequest.getPrice());
        room.setArea(roomRequest.getArea());
        room.setMaxPeople(roomRequest.getMaxPeople());
        room.setStatus(roomRequest.getStatus());
        room.setDescription(roomRequest.getDescription());

        return roomRepository.save(room);
    }

    @Override
    public void delete(Long id) {
        roomRepository.deleteById(id);
    }
}