package com.example.server.controllers;

import com.example.server.entity.ServiceEntity;
import com.example.server.services.ServiceService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // ================= GET ALL =================
    @GetMapping
    public List<ServiceEntity> getAll(Principal principal) {

        return serviceService.getAllByUser(
                principal.getName()
        );
    }

    // ================= CREATE =================
    @PostMapping
    public ServiceEntity create(
            @RequestBody ServiceEntity service,
            Principal principal
    ){

        return serviceService.create(
                service,
                principal.getName()
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ServiceEntity update(
            @PathVariable Long id,
            @RequestBody ServiceEntity service,
            Principal principal
    ) {

        return serviceService.update(
                id,
                service,
                principal.getName()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id,
            Principal principal
    ) {

        serviceService.delete(
                id,
                principal.getName()
        );

        return "Deleted successfully";
    }
}