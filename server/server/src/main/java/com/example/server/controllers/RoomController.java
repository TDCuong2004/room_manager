package com.example.server.controllers;

import com.example.server.dto.RoomServiceDTO;
import com.example.server.entity.Building;
import com.example.server.entity.Rooms;
import com.example.server.enums.RoomStatus;
import com.example.server.services.RoomService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.MediaType;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Thêm phòng
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Rooms create(
            @RequestParam String roomName,
            @RequestParam Double price,
            @RequestParam Double area,
            @RequestParam Integer maxPeople,
            @RequestParam String status,
            @RequestParam String description,
            @RequestParam Long buildingId,
            @RequestParam(required = false) List<MultipartFile> images
    ) {

        Rooms room = new Rooms();
        room.setRoomName(roomName);
        room.setPrice(BigDecimal.valueOf(price));
        room.setArea(BigDecimal.valueOf(area));
        room.setMaxPeople(maxPeople);
        room.setStatus(RoomStatus.valueOf(status));
        room.setDescription(description);

        Building b = new Building();
        b.setId(buildingId);
        room.setBuilding(b);

        return roomService.createWithImages(room, images);
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
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Rooms update(
            @PathVariable Long id,
            @RequestParam String roomName,
            @RequestParam Double price,
            @RequestParam Double area,
            @RequestParam Integer maxPeople,
            @RequestParam String status,
            @RequestParam String description,
            @RequestParam Long buildingId,
            @RequestParam(required = false) List<MultipartFile> images
    ) {

        Rooms room = new Rooms();
        room.setRoomName(roomName);
        room.setPrice(BigDecimal.valueOf(price));
        room.setArea(BigDecimal.valueOf(area));
        room.setMaxPeople(maxPeople);
        room.setStatus(RoomStatus.valueOf(status));
        room.setDescription(description);

        Building b = new Building();
        b.setId(buildingId);
        room.setBuilding(b);

        return roomService.updateWithImages(id, room, images);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roomService.delete(id);
    }
    @GetMapping("/{id}/services")
    public List<RoomServiceDTO> getServicesByRoom(@PathVariable Long id) {
        return roomService.getServicesByRoom(id);
    }

}