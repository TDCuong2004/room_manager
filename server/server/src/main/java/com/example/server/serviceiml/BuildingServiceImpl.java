package com.example.server.serviceiml;

import com.example.server.entity.Building;
import com.example.server.entity.User;
import com.example.server.repository.BuildingRepository;
import com.example.server.repository.UserRepository;
import com.example.server.services.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceImpl implements BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UserRepository userRepository;

    // lấy tất cả building của user
    @Override
    public List<Building> getAllByUser(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildingRepository.findByUser_Id(user.getId());
    }

    // tạo building
    @Override
    public Building create(Building building, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        building.setUser(user);
        building.setBuildingCode(null);
        // 👉 save lần 1
        Building saved = buildingRepository.save(building);
        // 👉 set code
        String code = "B" + String.format("%03d", saved.getId());
        saved.setBuildingCode(code);

        // 👉 save lần 2
        return buildingRepository.save(saved);
    }

    // update building
    @Override
    public Building update(Long id, Building building, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Building existing = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa building này");
        }

        existing.setBuildingCode(building.getBuildingCode());
        existing.setBuildingName(building.getBuildingName());
        existing.setAddress(building.getAddress());
        existing.setDescription(building.getDescription());

        return buildingRepository.save(existing);
    }

    // delete building
    @Override
    public void delete(Long id, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Building existing = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa building này");
        }

        buildingRepository.delete(existing);
    }
}