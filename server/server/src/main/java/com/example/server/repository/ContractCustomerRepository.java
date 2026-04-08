package com.example.server.repository;

import com.example.server.entity.ContractCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContractCustomerRepository extends JpaRepository<ContractCustomer, Long> {

    // ================= ĐẾM NGƯỜI TRONG PHÒNG =================
    @Query("""
        SELECT COUNT(cc.id)
        FROM ContractCustomer cc
        JOIN cc.contract c
        WHERE c.room.id = :roomId
        AND c.status = com.example.server.enums.ContractStatus.ACTIVE
    """)
    int countCurrentPeople(Long roomId);


    // ================= LẤY NGƯỜI ĐẠI DIỆN =================
    @Query("""
        SELECT cus.fullName
        FROM ContractCustomer cc
        JOIN cc.customer cus
        JOIN cc.contract ct
        WHERE ct.room.id = :roomId
        AND ct.status = com.example.server.enums.ContractStatus.ACTIVE
        AND cc.isRepresentative = true
    """)
    Optional<String> findRepresentativeName(Long roomId);


    // ================= LẤY DANH SÁCH NGƯỜI THEO CONTRACT =================
    List<ContractCustomer> findByContract_Id(Long contractId);


    // ================= ĐẾM NGƯỜI THEO CONTRACT (TỐI ƯU) =================
    int countByContract_Id(Long contractId);

}