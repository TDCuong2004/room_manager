package com.example.server.services;

import com.example.server.dto.InvoiceResponse;

import java.util.List;

public interface InvoiceService {

    void generateInvoices(Long buildingId, String month);
    public void updateStatus(Long id, String status, String paymentMethod);
    List<InvoiceResponse> getInvoices(Long buildingId, String month);
}