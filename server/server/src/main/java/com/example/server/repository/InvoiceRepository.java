package com.example.server.repository;

import com.example.server.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    boolean existsByRoomIdAndMonth(Long roomId, String month);
    Optional<InvoiceEntity> findByRoom_IdAndMonth(Long roomId, String month);
    List<InvoiceEntity> findByRoom_Building_IdAndMonth(Long buildingId, String month);
}