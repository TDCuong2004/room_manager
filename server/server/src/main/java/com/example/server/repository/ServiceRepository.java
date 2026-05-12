package com.example.server.repository;

import com.example.server.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    boolean existsByServiceName(String serviceName);
    List<ServiceEntity> findByUser_Id(Long userId);
    boolean existsByServiceNameAndUser_Id(
            String serviceName,
            Long userId
    );
}
