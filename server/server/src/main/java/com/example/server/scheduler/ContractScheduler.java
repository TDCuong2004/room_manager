package com.example.server.scheduler;

import com.example.server.entity.Contract;
import com.example.server.enums.ContractStatus;
import com.example.server.repository.ContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractScheduler {

    private final ContractRepository contractRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateContractStatus() {

        LocalDate today = LocalDate.now();

        List<Contract> all = contractRepository.findAll();

        for (Contract c : all) {

            if (!today.isBefore(c.getStartDate())
                    && !today.isAfter(c.getEndDate())) {

                c.setStatus(ContractStatus.ACTIVE);

            } else if (today.isBefore(c.getStartDate())) {

                c.setStatus(ContractStatus.PENDING);

            } else {

                c.setStatus(ContractStatus.EXPIRED);
            }
        }
    }
}
