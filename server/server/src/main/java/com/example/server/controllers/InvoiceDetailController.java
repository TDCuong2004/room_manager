package com.example.server.controllers;

import com.example.server.entity.InvoiceDetailEntity;
import com.example.server.repository.InvoiceDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/invoice-details")
@RequiredArgsConstructor
public class InvoiceDetailController {

    private final InvoiceDetailRepository repo;

    @GetMapping("/{invoiceId}")
    public List<InvoiceDetailEntity> get(@PathVariable Long invoiceId){
        return repo.findByInvoiceId(invoiceId);
    }
}
