package com.example.server.services;

import com.example.server.entity.ServiceEntity;
import java.util.List;

public interface ServiceService {
//    List<ServiceEntity> getAll();

    List<ServiceEntity> getAllByUser(String username);

    ServiceEntity getById(Long id);

    ServiceEntity create(ServiceEntity service, String username);

    ServiceEntity update(
            Long id,
            ServiceEntity service,
            String username
    );

    void delete(Long id, String username);
}