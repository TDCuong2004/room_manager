package com.example.server.serviceiml;

import com.example.server.entity.ServiceEntity;
import com.example.server.entity.User;
import com.example.server.repository.ServiceRepository;
import com.example.server.repository.UserRepository;
import com.example.server.services.ServiceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    public ServiceServiceImpl(
            ServiceRepository serviceRepository,
            UserRepository userRepository
    ) {
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ServiceEntity> getAllByUser(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return serviceRepository.findByUser_Id(user.getId());
    }

    @Override
    public ServiceEntity getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    @Override
    public ServiceEntity create(ServiceEntity service, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(serviceRepository.existsByServiceNameAndUser_Id(
                service.getServiceName(),
                user.getId()
        )) {
            throw new RuntimeException("Dịch vụ đã tồn tại");
        }

        service.setUser(user);

        ServiceEntity saved = serviceRepository.save(service);

        saved.setServiceCode("SV" + saved.getId());

        return serviceRepository.save(saved);
    }
    @Override
    public ServiceEntity update(
            Long id,
            ServiceEntity updatedService,
            String username
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ServiceEntity service = getById(id);

        // check owner
        if (!service.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền");
        }

        service.setServiceName(updatedService.getServiceName());
        service.setUnit(updatedService.getUnit());
        service.setCalculationType(updatedService.getCalculationType());

        return serviceRepository.save(service);
    }

    @Override
    public void delete(Long id, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ServiceEntity service = getById(id);

        // check owner
        if (!service.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền");
        }

        serviceRepository.delete(service);
    }
}