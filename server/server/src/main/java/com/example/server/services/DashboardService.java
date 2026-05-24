package com.example.server.services;

import com.example.server.dto.MonthlyRevenueDTO;
import com.example.server.dto.RevenueDashboardDTO;

import java.util.List;

public interface DashboardService {
    List<MonthlyRevenueDTO> getMonthlyRevenue();
    RevenueDashboardDTO getRevenue();
}
