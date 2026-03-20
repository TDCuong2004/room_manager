package com.example.server.controllers;

import com.example.server.entity.Rooms;
import com.example.server.services.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Thêm phòng
    @PostMapping
    public Rooms create(@RequestBody Rooms room) {
        return roomService.create(room);
    }

    // Lấy tất cả
    @GetMapping
    public List<Rooms> getAll() {
        return roomService.getAll();
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public Rooms getById(@PathVariable Long id) {
        return roomService.getById(id);
    }

    // Lấy theo building
    @GetMapping("/building/{buildingId}")
    public List<Rooms> getByBuilding(@PathVariable Long buildingId) {
        return roomService.getByBuilding(buildingId);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public Rooms update(@PathVariable Long id,
                        @RequestBody Rooms room) {
        return roomService.update(id, room);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roomService.delete(id);
    }
}