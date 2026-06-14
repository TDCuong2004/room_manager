package com.example.server.serviceiml;

import com.example.server.dto.MonthlyRevenueDTO;
import com.example.server.dto.RevenueDashboardDTO;
import com.example.server.enums.InvoiceStatus;
import com.example.server.repository.InvoiceDetailRepository;
import com.example.server.repository.InvoiceRepository;
import com.example.server.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final InvoiceDetailRepository invoiceDetailRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public RevenueDashboardDTO getRevenue() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        RevenueDashboardDTO dto = new RevenueDashboardDTO();

        dto.setTotalRevenue(
                invoiceDetailRepository.getTotalRevenue(
                        InvoiceStatus.PAID,
                        username
                )
        );

        dto.setRentRevenue(
                invoiceDetailRepository.getRentRevenue(
                        InvoiceStatus.PAID,
                        username
                )
        );

        dto.setServiceRevenue(
                invoiceDetailRepository.getServiceRevenue(
                        InvoiceStatus.PAID,
                        username
                )
        );

        return dto;
    }
    @Override
    public List<MonthlyRevenueDTO> getMonthlyRevenue() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return invoiceRepository.getMonthlyRevenue(
                InvoiceStatus.PAID,
                username
        );
    }
}