package com.example.server.scheduler;

import com.example.server.entity.Contract;
import com.example.server.entity.Rooms;
import com.example.server.enums.ContractStatus;
import com.example.server.enums.RoomStatus;
import com.example.server.repository.ContractRepository;
import com.example.server.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractScheduler {

    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;

    // chạy mỗi ngày lúc 00:00
//    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "*/10 * * * * ?")
    @Transactional
    public void updateContractStatus() {

        LocalDate today = LocalDate.now();

        List<Contract> all = contractRepository.findAll();

        for (Contract c : all) {

            // ================= ACTIVE =================
            if (!today.isBefore(c.getStartDate())
                    && !today.isAfter(c.getEndDate())) {

                c.setStatus(ContractStatus.ACTIVE);

                // 🔥 đảm bảo phòng đang có người → RENTED
                Rooms room = c.getRoom();
                if (room.getStatus() != RoomStatus.RENTED) {
                    room.setStatus(RoomStatus.RENTED);
                    roomRepository.save(room);
                }

            }
            // ================= PENDING =================
            else if (today.isBefore(c.getStartDate())) {

                c.setStatus(ContractStatus.PENDING);

            }
            // ================= EXPIRED =================
            else {

                c.setStatus(ContractStatus.EXPIRED);

                Rooms room = c.getRoom();

                // 🔥 check còn contract ACTIVE khác không
                boolean hasActive = contractRepository
                        .existsByRoomIdAndStatus(
                                room.getId(),
                                ContractStatus.ACTIVE
                        );

                // 🔥 nếu không còn ai thuê → phòng trống
                if (!hasActive) {
                    room.setStatus(RoomStatus.EMPTY);
                    roomRepository.save(room);
                }
            }
        }

        // 🔥 LƯU CONTRACT
        contractRepository.saveAll(all);
    }
}