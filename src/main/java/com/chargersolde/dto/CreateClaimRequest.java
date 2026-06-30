package com.chargersolde.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClaimRequest {

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String subject;
}