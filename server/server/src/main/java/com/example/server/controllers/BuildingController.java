package com.example.server.controllers;

import com.example.server.entity.Building;
import com.example.server.services.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@CrossOrigin
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    // Lấy building của user đang login
    @GetMapping
    public List<Building> getAll(Principal principal) {
        return buildingService.getAllByUser(principal.getName());
    }

    // Tạo
    @PostMapping
    public Building create(@RequestBody Building building,
                           Principal principal) {
        return buildingService.create(building, principal.getName());
    }

    // Update
    @PutMapping("/{id}")
    public Building update(@PathVariable Long id,
                           @RequestBody Building building,
                           Principal principal) {
        return buildingService.update(id, building, principal.getName());
    }

    // Delete
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       Principal principal) {
        buildingService.delete(id, principal.getName());
    }
}