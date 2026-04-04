package com.example.server.controllers;

import com.example.server.dto.InvoiceResponse;
import com.example.server.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public List<InvoiceResponse> getInvoices(
            @RequestParam Long buildingId,
            @RequestParam String month
    ) {
        return invoiceService.getInvoices(buildingId, month);
    }

    @PostMapping("/generate")
    public String generate(
            @RequestParam Long buildingId,
            @RequestParam String month
    ) {

        invoiceService.generateInvoices(buildingId, month);
        return "OK";
    }
}