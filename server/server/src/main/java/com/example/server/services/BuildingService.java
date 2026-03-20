package com.example.server.services;

import com.example.server.entity.Building;

import java.util.List;

public interface BuildingService {

    List<Building> getAllByUser(String username);

    Building create(Building building, String username);

    Building update(Long id, Building building, String username);

    void delete(Long id, String username);

}