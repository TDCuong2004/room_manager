package com.example.server.serviceiml;

import com.example.server.dto.BuildingServiceDTO;
import com.example.server.entity.BuildingService;
import com.example.server.repository.BuildingServiceRepository;
import com.example.server.services.BuildingServiceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceServiceImpl implements BuildingServiceService {

    private final BuildingServiceRepository repository;

    public BuildingServiceServiceImpl(BuildingServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BuildingServiceDTO> getByBuilding(Long buildingId) {

        return repository.findByBuildingId(buildingId)
                .stream()
                .filter(bs -> bs.getService().getCalculationType().name().equals("BY_METER"))
                .map(bs -> {

                    BuildingServiceDTO dto = new BuildingServiceDTO();

                    dto.setId(bs.getId());
                    dto.setPrice(bs.getPrice());

                    dto.setServiceId(bs.getService().getId());
                    dto.setServiceName(bs.getService().getServiceName());
                    dto.setCalculationType(
                            bs.getService().getCalculationType().name()
                    );

                    return dto;

                })
                .toList();
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