package com.example.server.serviceiml;

import com.example.server.entity.ServiceEntity;
import com.example.server.repository.ServiceRepository;
import com.example.server.services.ServiceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<ServiceEntity> getAll() {
        return serviceRepository.findAll();
    }

    @Override
    public ServiceEntity getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    @Override
    public ServiceEntity create(ServiceEntity service) {
        return serviceRepository.save(service);
    }

    @Override
    public ServiceEntity update(Long id, ServiceEntity updatedService) {

        ServiceEntity service = getById(id);

        service.setServiceCode(updatedService.getServiceCode());
        service.setServiceName(updatedService.getServiceName());
        service.setUnit(updatedService.getUnit());
        service.setCalculationType(updatedService.getCalculationType());

        return serviceRepository.save(service);
    }

    @Override
    public void delete(Long id) {
        ServiceEntity service = getById(id);
        serviceRepository.delete(service);
    }
}