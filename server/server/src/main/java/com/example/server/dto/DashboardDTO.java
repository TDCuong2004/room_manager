package com.example.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {

    private long totalUsers;
    private long totalBuildings;
    private long totalRooms;
    private long totalPosts;

    private List<MonthlyRevenueDTO> monthlyRevenue;

    private long newUsers;
    private long activePosts;
}
