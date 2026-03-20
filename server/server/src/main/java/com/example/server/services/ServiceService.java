package com.example.server.services;

import com.example.server.entity.ServiceEntity;
import java.util.List;

public interface ServiceService {

    List<ServiceEntity> getAll();

    ServiceEntity getById(Long id);

    ServiceEntity create(ServiceEntity service);

    ServiceEntity update(Long id, ServiceEntity service);

    void delete(Long id);
}