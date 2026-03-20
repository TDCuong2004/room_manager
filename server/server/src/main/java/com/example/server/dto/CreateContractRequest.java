package com.example.server.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateContractRequest {

    private Long roomId;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal deposit;
    private BigDecimal rentPrice;

    private List<CustomerContractDTO> customers;

}