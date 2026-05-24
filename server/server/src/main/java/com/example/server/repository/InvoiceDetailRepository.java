package com.example.server.repository;

import com.example.server.entity.InvoiceDetailEntity;
import com.example.server.enums.InvoiceStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetailEntity, Long> {

    @Transactional
    void deleteByInvoiceId(Long invoiceId);

    List<InvoiceDetailEntity> findByInvoiceId(Long invoiceId);

    // ================= TOTAL =================

    @Query("""
        SELECT COALESCE(SUM(d.amount),0)
        FROM InvoiceDetailEntity d
        JOIN d.invoice i
        JOIN i.room r
        JOIN r.building b
        JOIN b.user u
        WHERE i.status = :status
        AND u.username = :username
    """)
    Double getTotalRevenue(
            InvoiceStatus status,
            String username
    );

    // ================= RENT =================

    @Query("""
        SELECT COALESCE(SUM(d.amount),0)
        FROM InvoiceDetailEntity d
        JOIN d.invoice i
        JOIN i.room r
        JOIN r.building b
        JOIN b.user u
        WHERE i.status = :status
        AND u.username = :username
        AND LOWER(d.serviceName) = 'tiền phòng'
    """)
    Double getRentRevenue(
            InvoiceStatus status,
            String username
    );

    // ================= SERVICE =================

    @Query("""
        SELECT COALESCE(SUM(d.amount),0)
        FROM InvoiceDetailEntity d
        JOIN d.invoice i
        JOIN i.room r
        JOIN r.building b
        JOIN b.user u
        WHERE i.status = :status
        AND u.username = :username
        AND LOWER(d.serviceName) <> 'tiền phòng'
    """)
    Double getServiceRevenue(
            InvoiceStatus status,
            String username
    );
}
