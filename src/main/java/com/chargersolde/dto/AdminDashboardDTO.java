package com.chargersolde.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardDTO {


    private Long totalClients;


    private Long totalRecharges;


    private Long todayRecharges;


    private Long weekRecharges;


    private Long monthRecharges;

    private Double totalAmount;
}
