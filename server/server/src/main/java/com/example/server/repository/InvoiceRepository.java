package com.example.server.repository;

import com.example.server.dto.MonthlyRevenueDTO;
import com.example.server.entity.InvoiceEntity;
import com.example.server.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    boolean existsByRoomIdAndMonth(Long roomId, String month);
    Optional<InvoiceEntity> findByRoom_IdAndMonth(Long roomId, String month);
    List<InvoiceEntity> findByRoom_Building_IdAndMonth(Long buildingId, String month);
    List<InvoiceEntity> findTop5ByRoom_IdAndStatusOrderByPaidAtDesc(
            Long roomId,
            InvoiceStatus status
    );
    @Query("""
SELECT new com.example.server.dto.MonthlyRevenueDTO(
    i.month,
    SUM(i.totalAmount)
)
FROM InvoiceEntity i
WHERE i.status = :status
GROUP BY i.month
ORDER BY i.month
""")
    List<MonthlyRevenueDTO> getMonthlyRevenueForAdmin(
            InvoiceStatus status
    );

    @Query("""
SELECT new com.example.server.dto.MonthlyRevenueDTO(
    i.month,
    SUM(i.totalAmount)
)
FROM InvoiceEntity i
JOIN i.room r
JOIN r.building b
JOIN b.user u
WHERE i.status = :status
AND u.username = :username
GROUP BY i.month
ORDER BY i.month
""")
    List<MonthlyRevenueDTO> getMonthlyRevenue(
            InvoiceStatus status,
            String username
    );
}