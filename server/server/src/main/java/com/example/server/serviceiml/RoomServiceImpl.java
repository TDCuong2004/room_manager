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

        List<Rooms> rooms = roomRepository.findByBuildingId(buildingId);

        for (Rooms room : rooms) {

            int count = contractCustomerRepository.countCurrentPeople(room.getId());

            room.setCurrentPeople(count); // 👈 gán vào
        }

        return rooms;
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
    @Override
    public List<RoomServiceDTO> getServicesByRoom(Long roomId) {

        Rooms room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Long buildingId = room.getBuilding().getId();

        return buildingServiceRepository.getServicesByBuilding(buildingId);
    }
    @Override
    public Rooms createWithImages(Rooms room, List<MultipartFile> images) {

        Rooms savedRoom = roomRepository.save(room);

        if(images != null){
            for(MultipartFile file : images){
                try{

                    // tạo thư mục nếu chưa có
                    File dir = new File("uploads");
                    if(!dir.exists()) dir.mkdirs();

                    // tên file
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                    File dest = new File("uploads/" + fileName);
                    file.transferTo(dest);

                    // lưu DB
                    RoomImage img = new RoomImage();
                    img.setRoom(savedRoom);
                    img.setImageUrl("http://localhost:3000/uploads/" + fileName);

                    roomImageRepository.save(img);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        return savedRoom;
    }

    @Override
    public Rooms updateWithImages(Long id, Rooms roomRequest, List<MultipartFile> images) {

        Rooms room = getById(id);

        // update field
        room.setRoomCode(roomRequest.getRoomCode());
        room.setRoomName(roomRequest.getRoomName());
        room.setPrice(roomRequest.getPrice());
        room.setArea(roomRequest.getArea());
        room.setMaxPeople(roomRequest.getMaxPeople());
        room.setStatus(roomRequest.getStatus());
        room.setDescription(roomRequest.getDescription());

        Rooms savedRoom = roomRepository.save(room);

        // nếu có ảnh mới thì thêm
        if(images != null){
            for(MultipartFile file : images){
                try{
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                    File dest = new File("uploads/" + fileName);
                    file.transferTo(dest);

                    RoomImage img = new RoomImage();
                    img.setRoom(savedRoom);
                    img.setImageUrl("http://localhost:3000/uploads/" + fileName);

                    roomImageRepository.save(img);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        return savedRoom;
    }
}