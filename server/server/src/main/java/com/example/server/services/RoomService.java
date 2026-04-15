package com.example.server.services;

import com.example.server.dto.RoomServiceDTO;
import com.example.server.entity.Rooms;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RoomService {

    Rooms create(Rooms room);

    List<Rooms> getAll();

    Rooms getById(Long id);

    List<Rooms> getByBuilding(Long buildingId);

    Rooms update(Long id, Rooms room);
    void deleteImage(Long imageId);
    void delete(Long id);
    List<RoomServiceDTO> getServicesByRoom(Long roomId);
    Rooms createWithImages(Rooms room, List<MultipartFile> images);
    Rooms updateWithImages(
            Long id,
            Rooms room,
            List<MultipartFile> images,
            List<String> keepImages
    );
}