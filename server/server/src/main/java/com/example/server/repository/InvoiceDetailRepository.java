package com.example.server.repository;

import com.example.server.entity.InvoiceDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetailEntity, Long> {

    List<InvoiceDetailEntity> findByInvoiceId(Long invoiceId);
}
