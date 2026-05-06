package com.example.server.repository;

import com.example.server.entity.InvoiceDetailEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetailEntity, Long> {
    @Transactional
    void deleteByInvoiceId(Long invoiceId);
    List<InvoiceDetailEntity> findByInvoiceId(Long invoiceId);
}
