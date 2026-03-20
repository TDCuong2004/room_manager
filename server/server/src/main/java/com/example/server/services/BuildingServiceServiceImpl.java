package com.example.server.services;

import com.example.server.entity.BuildingService;
import com.example.server.repository.BuildingServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceServiceImpl implements BuildingServiceService {

    private final BuildingServiceRepository repository;

    public BuildingServiceServiceImpl(BuildingServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BuildingService> getByBuilding(Long buildingId) {
        return repository.findByBuildingId(buildingId);
    }

    @Override
    public BuildingService add(BuildingService entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}