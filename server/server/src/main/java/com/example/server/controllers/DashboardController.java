package com.example.server.controllers;

import com.example.server.dto.MonthlyRevenueDTO;
import com.example.server.dto.RevenueDashboardDTO;
import com.example.server.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/revenue")
    public RevenueDashboardDTO getRevenue() {
        return dashboardService.getRevenue();
    }

    @GetMapping("/monthly-revenue")
    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        return dashboardService.getMonthlyRevenue();
    }
}
