package com.chargersolde.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class ClientBalanceResponse {


    private Long clientId;

    private String email;

    private LocalDate startDate;

    private LocalDate endDate;
    private Double totalBalance;

    private Integer totalOrders;

    private List<DailyBalanceDTO> details;

}