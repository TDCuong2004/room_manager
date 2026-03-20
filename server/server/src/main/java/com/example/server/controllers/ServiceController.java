package com.example.server.controllers;

import com.example.server.entity.ServiceEntity;
import com.example.server.services.ServiceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // Lấy tất cả service
    @GetMapping
    public List<ServiceEntity> getAll() {
        return serviceService.getAll();
    }

    // Tạo service mới
    @PostMapping
    public ServiceEntity create(@RequestBody ServiceEntity service){
        return serviceService.create(service);
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ServiceEntity update(@PathVariable Long id,
                                @RequestBody ServiceEntity service) {
        return serviceService.update(id, service);
    }

    // Xoá
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        serviceService.delete(id);
        return "Deleted successfully";
    }
}
