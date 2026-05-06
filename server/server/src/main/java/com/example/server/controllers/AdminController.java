package com.example.server.controllers;

import com.example.server.dto.DashboardDTO;
import com.example.server.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public DashboardDTO getDashboard() {
        return adminService.getDashboard();
    }
}
