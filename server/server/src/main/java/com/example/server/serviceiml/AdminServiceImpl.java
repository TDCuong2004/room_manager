package com.example.server.serviceiml;

import com.example.server.dto.DashboardDTO;
import com.example.server.enums.PostStatus;
import com.example.server.enums.UserRole;
import com.example.server.repository.*;
import com.example.server.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final PostRepository postRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public DashboardDTO getDashboard() {

        DashboardDTO dto = new DashboardDTO();

        // ================= KPI =================
        dto.setTotalUsers(userRepository.countByRole(UserRole.USER));
        dto.setTotalBuildings(buildingRepository.count());
        dto.setTotalRooms(roomRepository.count());
        dto.setTotalPosts(postRepository.count());

        // ================= REVENUE =================
        dto.setMonthlyRevenue(invoiceRepository.getMonthlyRevenue());

        // ================= GROWTH =================
        dto.setNewUsers(5); // 👉 tạm, sau có thể tính theo tháng
        dto.setActivePosts(postRepository.countByStatus(PostStatus.PUBLISHED));

        return dto;
    }
}
