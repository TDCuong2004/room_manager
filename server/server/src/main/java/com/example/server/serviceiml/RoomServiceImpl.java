package com.example.server.serviceiml;

import com.example.server.dto.RoomServiceDTO;
import com.example.server.entity.RoomImage;
import com.example.server.entity.Rooms;
import com.example.server.repository.BuildingServiceRepository;
import com.example.server.repository.ContractCustomerRepository;
import com.example.server.repository.RoomImageRepository;
import com.example.server.repository.RoomRepository;
import com.example.server.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private final ContractCustomerRepository contractCustomerRepository;
    private final RoomRepository roomRepository;
    private final BuildingServiceRepository buildingServiceRepository;

    @Autowired
    private RoomImageRepository roomImageRepository;

    public RoomServiceImpl(RoomRepository roomRepository,
                           BuildingServiceRepository buildingServiceRepository,
                           ContractCustomerRepository contractCustomerRepository) {
        this.roomRepository = roomRepository;
        this.buildingServiceRepository = buildingServiceRepository;
        this.contractCustomerRepository = contractCustomerRepository;
    }

    @Override
    public Rooms create(Rooms room) {

        // 🔥 CHECK TRÙNG
        boolean exists = roomRepository.existsByRoomNameAndBuilding_Id(
                room.getRoomName(),
                room.getBuilding().getId()
        );

        if (exists) {
            throw new RuntimeException("Tên phòng đã tồn tại");
        }

        room.setRoomCode(null);

        Rooms saved = roomRepository.save(room);

        String code = "R" + String.format("%03d", saved.getId());
        saved.setRoomCode(code);

        return roomRepository.save(saved);
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
        List<Rooms> rooms = roomRepository.findByBuildingId(buildingId);

        for (Rooms room : rooms) {
            int count = contractCustomerRepository.countCurrentPeople(room.getId());
            room.setCurrentPeople(count);
        }

        return rooms;
    }

    @Override
    public Rooms update(Long id, Rooms roomRequest) {

        Rooms room = getById(id);

        boolean exists = roomRepository.existsByRoomNameAndBuilding_Id(
                roomRequest.getRoomName(),
                room.getBuilding().getId()
        );

        if (exists && !room.getRoomName().equals(roomRequest.getRoomName())) {
            throw new RuntimeException("Tên phòng đã tồn tại");
        }

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

    @Override
    public List<RoomServiceDTO> getServicesByRoom(Long roomId) {
        Rooms room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Long buildingId = room.getBuilding().getId();
        return buildingServiceRepository.getServicesByBuilding(buildingId);
    }

    // ================= CREATE WITH IMAGES =================
    @Override
    public Rooms createWithImages(Rooms room, List<MultipartFile> images) {

        // 🔥 CHECK TRÙNG
        boolean exists = roomRepository.existsByRoomNameAndBuilding_Id(
                room.getRoomName(),
                room.getBuilding().getId()
        );

        if (exists) {
            throw new RuntimeException("Tên phòng đã tồn tại");
        }

        Rooms savedRoom = roomRepository.save(room);

        if (images != null) {

            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            for (MultipartFile file : images) {
                try {

                    if (file.isEmpty()) continue;

                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                    File dest = new File(uploadDir + fileName);
                    file.transferTo(dest);

                    RoomImage img = new RoomImage();
                    img.setRoom(savedRoom);
                    img.setImageUrl("/uploads/" + fileName);

                    roomImageRepository.save(img);

                } catch (Exception e) {
                    System.out.println("UPLOAD ERROR:");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        return savedRoom;
    }

    // ================= UPDATE WITH IMAGES =================
    public Rooms updateWithImages(
            Long id,
            Rooms roomRequest,
            List<MultipartFile> images,
            List<String> keepImages
    ) {

        Rooms room = getById(id);

        // 🔥 CHECK TRÙNG (THÊM ĐOẠN NÀY)
        boolean exists = roomRepository.existsByRoomNameAndBuilding_Id(
                roomRequest.getRoomName(),
                room.getBuilding().getId()
        );

        if (exists && !room.getRoomName().equals(roomRequest.getRoomName())) {
            throw new RuntimeException("Tên phòng đã tồn tại");
        }

        // ================= UPDATE INFO =================
        room.setRoomName(roomRequest.getRoomName());
        room.setPrice(roomRequest.getPrice());
        room.setArea(roomRequest.getArea());
        room.setMaxPeople(roomRequest.getMaxPeople());
        room.setStatus(roomRequest.getStatus());
        room.setDescription(roomRequest.getDescription());

        Rooms savedRoom = roomRepository.save(room);

        String uploadDir = System.getProperty("user.dir") + "/uploads/";

        // ================= XÓA ẢNH CŨ =================
        List<RoomImage> currentImages = roomImageRepository.findByRoomId(id);

        for (RoomImage img : currentImages) {
            if (keepImages == null || !keepImages.contains(img.getImageUrl())) {
                roomImageRepository.delete(img);
            }
        }

        // ================= THÊM ẢNH MỚI =================
        if (images != null) {

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            for (MultipartFile file : images) {
                try {

                    if (file.isEmpty()) continue;

                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                    File dest = new File(uploadDir + fileName);
                    file.transferTo(dest);

                    RoomImage img = new RoomImage();
                    img.setRoom(savedRoom);
                    img.setImageUrl("/uploads/" + fileName);

                    roomImageRepository.save(img);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        return savedRoom;
    }
    @Override
    public void deleteImage(Long imageId) {
        roomImageRepository.deleteById(imageId);
    }
}