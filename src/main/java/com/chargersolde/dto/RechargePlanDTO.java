package com.chargersolde.dto;

import lombok.Data;

@Data
public class RechargePlanDTO {
    private Long id;
    private String label;
    private Double price;
    private Integer validityDays;
    private Long operatorId;
}