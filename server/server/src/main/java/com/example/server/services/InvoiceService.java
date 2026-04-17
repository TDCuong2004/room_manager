package com.example.server.services;

import com.example.server.dto.InvoiceResponse;
import com.example.server.dto.PaidInvoiceDTO;
import com.example.server.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceService {

    void generateInvoices(Long buildingId, String month);
    public void updateStatus(Long id, String status, String paymentMethod);
    List<InvoiceResponse> getInvoices(Long buildingId, String month);
    List<PaidInvoiceDTO> getPaidInvoicesByRoom(Long roomId);
}