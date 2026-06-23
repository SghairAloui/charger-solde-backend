package com.chargersolde.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRechargeRequestDTO {

    @NotBlank
    private String phoneNumber;

    @NotNull
    private Long planId;

    @NotNull
    private Double amount;
}